package de.codebucket.mkkm.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import de.codebucket.mkkm.BuildConfig;
import de.codebucket.mkkm.KKMWebViewClient;
import de.codebucket.mkkm.R;

public abstract class WebViewActivity extends AppCompatActivity implements KKMWebViewClient.OnPageChangedListener {

    protected WebView mWebview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setupView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load webview layout
        SwipeRefreshLayout swipe = findViewById(R.id.swipe);
        swipe.setColorSchemeColors(getResources().getColor(R.color.colorAccentFallback));

        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mWebview = findViewById(R.id.webview);
        mWebview.setWebViewClient(new KKMWebViewClient(this, this));
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setDomStorageEnabled(true);
        mWebview.getSettings().setAppCacheEnabled(true);
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebview.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebview.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mWebview.restoreState(savedInstanceState);
    }
}
