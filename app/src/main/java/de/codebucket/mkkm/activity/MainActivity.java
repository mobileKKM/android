package de.codebucket.mkkm.activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.R;
import de.codebucket.mkkm.database.model.Account;
import de.codebucket.mkkm.database.model.AccountDao;
import de.codebucket.mkkm.database.model.Photo;
import de.codebucket.mkkm.database.model.PhotoDao;
import de.codebucket.mkkm.login.LoginHelper;
import de.codebucket.mkkm.login.UserLoginTask;
import de.codebucket.mkkm.util.Const;

import static de.codebucket.mkkm.KKMWebViewClient.getPageUrl;

public class MainActivity extends DrawerActivity implements UserLoginTask.OnCallbackListener {

    private static final String WEBAPP_URL = getPageUrl("home");

    private Account mAccount;
    private UserLoginTask mAuthTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.title_activity_main);

        // Set up navigation drawer
        setupDrawer(toolbar);

        // Get user account from login
        mAccount = (Account) getIntent().getSerializableExtra("account");
        setupDrawerHeader(mAccount);

        // Set up webview layout
        setupWebView();

        // Load additional data from database and inject webapp
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                PhotoDao photoDao = MobileKKM.getDatabase().photoDao();
                Photo photo = photoDao.getById(mAccount.getPhotoId());

                // Set drawer header background if photo exists
                if (photo != null) {
                    setupDrawerBackground(photo.getBitmap());
                }

                // Execute login and inject webapp
                injectWebapp();
            }
        });
    }

    public void injectWebapp() {
        if (mAuthTask != null) {
            return;
        }

        mAuthTask = new UserLoginTask(this);
        mAuthTask.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if token has expired and re-inject
        if (MobileKKM.getLoginHelper().hasSessionExpired()) {
            injectWebapp();
        }

        MobileKKM.getInstance().setupTicketService();
    }

    @Override
    public Object onPostLogin() throws IOException {
        LoginHelper loginHelper = MobileKKM.getLoginHelper();

        // Always fetch user account to update
        Account newAccount = loginHelper.getAccount();

        AccountDao accountDao = MobileKKM.getDatabase().accountDao();
        accountDao.insert(newAccount);

        // Check if photoId has changed
        PhotoDao photoDao = MobileKKM.getDatabase().photoDao();
        Photo photo = new Photo(null, null, null);

        if (!mAccount.getPhotoId().equals(newAccount.getPhotoId())) {
            photo = photoDao.getById(newAccount.getPhotoId());

            // Check if photo isn't null, otherwise fetch from website
            if (photo == null || photo.getBitmap() == null) {
                photo = MobileKKM.getLoginHelper().getPhoto(newAccount);
                photoDao.insert(photo);
            }

            // Remove old photo from database
            photoDao.deleteById(mAccount.getPhotoId());
        } else if (photoDao.getById(mAccount.getPhotoId()) == null) {
            // Current photo isn't stored in database
            photo = MobileKKM.getLoginHelper().getPhoto(newAccount);
            photoDao.insert(photo);
        }

        mAccount = newAccount;
        return photo;
    }

    @Override
    public void onSuccess(Object result) {
        mAuthTask = null;

        // Update drawer header
        setupDrawerHeader(mAccount);

        // Check if photo has changed and update
        Photo photo = (Photo) result;
        if (photo.getBitmap() != null) {
            setupDrawerBackground(photo.getBitmap());
        }

        // First inject session data into webview local storage, then load the webapp
        String startUrl = mWebview.getUrl() == null ? WEBAPP_URL : mWebview.getUrl();
        String inject = "<script type='text/javascript'>" +
                "localStorage.setItem('fingerprint', '" + MobileKKM.getLoginHelper().getFingerprint() + "');" +
                "localStorage.setItem('token', '" + MobileKKM.getLoginHelper().getSessionToken() + "');" +
                "window.location.replace('" + startUrl + "');" +
                "</script>";
        mWebview.loadDataWithBaseURL("https://m.kkm.krakow.pl/inject", inject, "text/html", "utf-8", null);
    }

    @Override
    public void onError(int errorCode, String message) {
        mAuthTask = null;

        if (errorCode == Const.ErrorCode.LOGIN_ERROR) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            doLogout();
        } else {
            Snackbar.make(findViewById(R.id.swipe), Const.getErrorMessage(errorCode, null), Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.snackbar_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            injectWebapp();
                        }
                    })
                    .setActionTextColor(Color.YELLOW)
                    .show();
        }
    }
}
