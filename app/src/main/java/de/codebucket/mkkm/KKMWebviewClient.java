package de.codebucket.mkkm;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.codebucket.mkkm.ui.MainActivity;

public class KKMWebviewClient extends WebViewClient {

    private static final String TAG = "WebviewClient";
    private static final String WEBAPP_URL = "https://m.kkm.krakow.pl";

    private Context mContext;
    private SwipeRefreshLayout mSwipeLayout;
    private NavigationView mNavigationView;

    private boolean hasInjected = false;
    private Map<String, Integer> navIds = new HashMap<String, Integer>(){{
        // R.id.nav_tickets
        put("home", R.id.nav_tickets);
        put("control", R.id.nav_tickets);

        // R.id.nav_purchase
        put("ticket", R.id.nav_purchase);

        // R.id.nav_account
        put("account", R.id.nav_account);
    }};

    public KKMWebviewClient(MainActivity context) {
        mContext = context;
        mSwipeLayout = (SwipeRefreshLayout) context.findViewById(R.id.swipe);
        mNavigationView = (NavigationView) context.findViewById(R.id.nav_view);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        mSwipeLayout.setEnabled(true);
        mSwipeLayout.setRefreshing(true);

        // Reset injection if url is webapp
        if (url.startsWith(WEBAPP_URL)) {
            hasInjected = false;
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        mSwipeLayout.setRefreshing(false);
        mSwipeLayout.setEnabled(false);

        // Remove navbar after page has finished loading
        if (url.startsWith(WEBAPP_URL) && !hasInjected) {
            AssetManager assetManager = mContext.getAssets();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = null;
            String inject = null;

            try {
                // Read webview.js from local assets
                inputStream = assetManager.open("webview.js");
                byte buf[] = new byte[8192];
                int len;
                while ((len = inputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }

                outputStream.close();
                inputStream.close();
                inject = outputStream.toString();
            } catch (IOException ex) {
                Log.e(TAG, "Error injecting script: " + ex);
                ex.printStackTrace();
            }

            view.addJavascriptInterface(new ScriptInjectorCallback(), "ScriptInjector");
            view.evaluateJavascript(inject, null);
        }

        // Set navigation item if page has changed
        String key = url.substring(WEBAPP_URL.length() + 4).split("/")[0];
        if (navIds.containsKey(key)) {
            MenuItem item = mNavigationView.getMenu().findItem(navIds.get(key));
            if (item != null && !item.isChecked()) {
                ((MainActivity) mContext).setTitle(item.getTitle());
                mNavigationView.setCheckedItem(item);
            }
        }
    }

    public static String getPageUrl(String page) {
        return String.format("%s/#!/%s", WEBAPP_URL, page);
    }

    public class ScriptInjectorCallback {
        @JavascriptInterface
        public void callback() {
            Log.d(TAG, "Script injected!");
            hasInjected = true;
        }
    }
}
