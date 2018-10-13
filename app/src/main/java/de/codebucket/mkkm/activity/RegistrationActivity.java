package de.codebucket.mkkm.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.Nullable;

import de.codebucket.mkkm.KKMWebViewClient;
import de.codebucket.mkkm.R;

public class RegistrationActivity extends WebViewActivity {

    public static final String EXTRA_REGISTRATION_COMPLETE = "registrationComplete";

    // for file uploading
    private static final int FILE_CHOOSER_RESULT_CODE = 100;
    private ValueCallback<Uri[]> mFilePathCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        setupView();
        setTitle(R.string.title_activity_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWebview.setWebChromeClient(new RegistrationWebChromeClient());
        mWebview.loadUrl(KKMWebViewClient.getPageUrl("register"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Uri[] results = null;

        // Check that the response is a good one
        if (resultCode == Activity.RESULT_OK && data != null) {
            String dataString = data.getDataString();
            if (dataString != null) {
                results = new Uri[]{ Uri.parse(dataString) };
            }
        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
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

    private class RegistrationWebChromeClient extends WebChromeClient {

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }

            mFilePathCallback = filePathCallback;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");

            startActivityForResult(Intent.createChooser(intent, null), FILE_CHOOSER_RESULT_CODE);
            return true;
        }
    }
}
