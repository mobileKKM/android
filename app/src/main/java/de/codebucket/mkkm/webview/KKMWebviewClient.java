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
        mSwipeLayout.setRefreshing(false);
        mSwipeLayout.setEnabled(false);

        // Remove navbar after page has finished loading
        if (url.endsWith("/home")) {
            String inject = "var allElements = document.getElementsByTagName('*');\n" +
                            "for (var i = 0, n = allElements.length; i < n; i++) {\n" +
                                "if (allElements[i].getAttribute('ng-controller') === 'NavbarCtrl') {\n" +
                                    "allElements[i].parentNode.removeChild(allElements[i]); break;\n" +
                                "}\n" +
                            "}";
            view.evaluateJavascript(inject, null);
        }
    }
}
