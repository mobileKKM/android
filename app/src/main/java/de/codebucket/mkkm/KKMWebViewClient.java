package de.codebucket.mkkm;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class KKMWebViewClient extends WebViewClient {

    private static final String TAG = "WebviewClient";
    private static final String WEBAPP_URL = "https://m.kkm.krakow.pl";

    public static final String PAGE_OVERVIEW = "home";
    public static final String PAGE_CONTROL = "control";
    public static final String PAGE_PURCHASE = "ticket";
    public static final String PAGE_ACCOUNT = "account";

    private Context mContext;
    private SwipeRefreshLayout mSwipeLayout;
    private OnPageChangedListener mPageListener;

    private boolean hasInjected = false;

    public KKMWebViewClient(Activity context, OnPageChangedListener listener) {
        mContext = context;
        mSwipeLayout = context.findViewById(R.id.swipe);
        mPageListener = listener;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        mSwipeLayout.setEnabled(true);
        mSwipeLayout.setRefreshing(true);

        // Reset injection if url is webapp
        if (url.startsWith(WEBAPP_URL)) {
            view.addJavascriptInterface(new ScriptInjectorCallback(), "ScriptInjector");
            hasInjected = false;
        }
    }

    @Override
    public void onReceivedError(final WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);

        // Ignore error if it isn't our main page
        if (!request.isForMainFrame()) {
            return;
        }

        mSwipeLayout.setRefreshing(false);
        mSwipeLayout.setEnabled(false);

        Snackbar.make(mSwipeLayout, R.string.error_no_network, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.snackbar_retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.reload();
                    }
                })
                .setActionTextColor(Color.YELLOW)
                .show();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        mSwipeLayout.setRefreshing(false);
        mSwipeLayout.setEnabled(false);

        // Enable zoom controls if it's the pricing list
        view.getSettings().setBuiltInZoomControls(url.endsWith("mobilekkm/cennik.html"));

        // Don't continue if it's not our webapp
        final String baseUrl = getPageUrl("");
        if (!url.startsWith(WEBAPP_URL)) {
            return;
        }

        // Remove navbar after page has finished loading
        if (!hasInjected) {
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

            view.evaluateJavascript(inject, null);
        }

        String page = url.substring(baseUrl.length()).split("/")[0];
        mPageListener.onPageChanged(view, page);
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

    public interface OnPageChangedListener {
        void onPageChanged(WebView view, String page);
    }
}
