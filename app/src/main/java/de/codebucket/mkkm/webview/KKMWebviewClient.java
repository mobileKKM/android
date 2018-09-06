package de.codebucket.mkkm.webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import de.codebucket.mkkm.R;
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
        loadJavascript(view, "https://code.jquery.com/jquery-3.3.1.min.js");
        loadJavascript(view, "https://www.razex.de/webview.js?t=" + System.currentTimeMillis());

        mSwipeLayout.setRefreshing(false);
        mSwipeLayout.setEnabled(false);
    }

    private void loadJavascript(WebView view, String url) {
        String inject = "var fileref = document.createElement('script');\n" +
                "fileref.setAttribute('src', '" + url + "');\n" +
                "fileref.setAttribute('type', 'text/javascript');\n" +
                "document.getElementsByTagName('head')[0].appendChild(fileref);";
        view.evaluateJavascript(inject, null);
    }

    private void loadStylesheet(WebView view, String url) {
        String inject = "var fileref = document.createElement('link');\n" +
                "fileref.setAttribute('href', '" + url + "');\n" +
                "fileref.setAttribute('type', 'text/css');\n" +
                "fileref.setAttribute('rel', 'stylesheet');\n" +
                "document.getElementsByTagName('head')[0].appendChild(fileref);";
        view.evaluateJavascript(inject, null);
    }
}
