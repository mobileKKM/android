package de.codebucket.mkkm.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import de.codebucket.mkkm.KKMWebViewClient;
import de.codebucket.mkkm.R;

public class RegistrationActivity extends WebViewActivity {

    public static final String EXTRA_REGISTRATION_COMPLETE = "registrationComplete";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.title_activity_registration);

        // Set up webview layout
        setupWebView();

        // Load registration form
        mWebview.loadUrl(KKMWebViewClient.getPageUrl("register"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageChanged(WebView view, String page) {
        if (page.equalsIgnoreCase("login")) {
            Intent result = new Intent();
            result.putExtra(EXTRA_REGISTRATION_COMPLETE, true);
            setResult(Activity.RESULT_OK, result);
            finish();
        }
    }
}
