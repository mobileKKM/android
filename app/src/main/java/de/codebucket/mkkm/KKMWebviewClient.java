package de.codebucket.mkkm;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.codebucket.mkkm.ui.MainActivity;

public class KKMWebviewClient extends WebViewClient {

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
        if (url.endsWith("/home")) {
            AssetManager assetManager = mContext.getAssets();
            ByteArrayOutputStream outputStream = null;
            InputStream inputStream = null;
            try {
                inputStream = assetManager.open("webview.js");
                outputStream = new ByteArrayOutputStream();
                byte buf[] = new byte[8192];
                int len;
                try {
                    while ((len = inputStream.read(buf)) != -1) {
                        outputStream.write(buf, 0, len);
                    }
                    outputStream.close();
                    inputStream.close();
                } catch (IOException e) {}
            } catch (IOException e) {}
            view.evaluateJavascript(outputStream.toString(), null);
        }
    }
}
