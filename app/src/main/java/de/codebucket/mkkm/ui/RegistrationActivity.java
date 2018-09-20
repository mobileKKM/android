package de.codebucket.mkkm.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import de.codebucket.mkkm.BuildConfig;
import de.codebucket.mkkm.KKMWebviewClient;
import de.codebucket.mkkm.R;

public class RegistrationActivity extends AppCompatActivity implements KKMWebviewClient.OnPageChangedListener {

    private WebView mWebview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.title_activity_registration);

        // Load webview layout
        SwipeRefreshLayout swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setColorSchemeColors(getResources().getColor(R.color.colorAccentFallback));

        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mWebview = (WebView) findViewById(R.id.webview);
        mWebview.setWebViewClient(new KKMWebviewClient(this, this));
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setDomStorageEnabled(true);
        mWebview.loadUrl(KKMWebviewClient.getPageUrl("register"));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh:
                mWebview.reload();
                return true;
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageChanged(WebView view, String page) {
        if (page.equalsIgnoreCase("login")) {
            // show warning here
        }
    }
}
