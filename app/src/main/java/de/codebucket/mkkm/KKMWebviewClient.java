package de.codebucket.mkkm;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.codebucket.mkkm.ui.MainActivity;

public class KKMWebviewClient extends WebViewClient {

    private static final String TAG = "WebviewClient";
    private static final String WEBAPP_URL = "https://m.kkm.krakow.pl";

    private Context mContext;
    private SwipeRefreshLayout mSwipeLayout;

    public KKMWebviewClient(MainActivity context) {
        mContext = context;
        mSwipeLayout = (SwipeRefreshLayout) context.findViewById(R.id.swipe);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        mSwipeLayout.setEnabled(true);
        mSwipeLayout.setRefreshing(true);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        mSwipeLayout.setRefreshing(false);
        mSwipeLayout.setEnabled(false);

        // Remove navbar after page has finished loading
        if (url.startsWith(WEBAPP_URL)) {
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
    }

    public static String getPageUrl(String page) {
        return String.format("%s/#!/%s", WEBAPP_URL, page);
    }
}
