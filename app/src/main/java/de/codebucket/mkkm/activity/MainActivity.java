package de.codebucket.mkkm.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import de.codebucket.mkkm.KKMWebViewClient;
import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.R;
import de.codebucket.mkkm.database.model.Account;
import de.codebucket.mkkm.database.model.Photo;
import de.codebucket.mkkm.database.model.PhotoDao;
import de.codebucket.mkkm.login.AccountUtils;
import de.codebucket.mkkm.login.UserLoginTask;
import de.codebucket.mkkm.util.Const;
import de.codebucket.mkkm.util.PicassoDrawable;

public class MainActivity extends DrawerActivity implements UserLoginTask.OnCallbackListener {

    private static final String TAG = "Main";

    private Account mAccount;
    private UserLoginTask mAuthTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupView();

        // Get user account from login
        mAccount = (Account) getIntent().getSerializableExtra("account");
        View headerView = mNavigationView.getHeaderView(0);

        TextView drawerUsername = (TextView) headerView.findViewById(R.id.drawer_header_username);
        drawerUsername.setText(String.format("%s %s", mAccount.getFirstName(), mAccount.getLastName()));

        TextView drawerEmail = (TextView) headerView.findViewById(R.id.drawer_header_email);
        drawerEmail.setText(mAccount.getEmail());
    }

    public void injectWebapp() {
        if (mAuthTask != null) {
            return;
        }

        mAuthTask = new UserLoginTask(this);
        mAuthTask.execute();
    }

    private void logout() {
        AccountUtils.removeAccount(AccountUtils.getAccount());

        // Return back to login screen
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_tickets:
                mWebview.loadUrl("https://m.kkm.krakow.pl/#!/home");
                break;
            case R.id.nav_purchase:
                mWebview.loadUrl("https://m.kkm.krakow.pl/#!/ticket/buy");
                break;
            case R.id.nav_account:
                mWebview.loadUrl("https://m.kkm.krakow.pl/#!/account");
                break;
            case R.id.nav_pricing:
                mWebview.loadUrl("https://m.kkm.krakow.pl/instructions/CENNIK.pdf");
                break;
            case R.id.nav_backup:
                Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_logout_title)
                        .setMessage(R.string.dialog_logout_warning)
                        .setNegativeButton(R.string.dialog_no, null)
                        .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                            }
                        })
                        .show();
                break;
            case R.id.nav_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }

        // Change title only on checkable items
        if (item.isCheckable()) {
            setTitle(item.getTitle());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPageChanged(WebView view, String page) {
        MenuItem item = null;

        switch (page) {
            case KKMWebViewClient.PAGE_OVERVIEW:
            case KKMWebViewClient.PAGE_CONTROL:
                item = mNavigationView.getMenu().findItem(R.id.nav_tickets);
                break;
            case KKMWebViewClient.PAGE_PURCHASE:
                item = mNavigationView.getMenu().findItem(R.id.nav_purchase);
                break;
            case KKMWebViewClient.PAGE_ACCOUNT:
                item = mNavigationView.getMenu().findItem(R.id.nav_account);
                break;
        }

        if (item != null && !item.isChecked()) {
            mNavigationView.setCheckedItem(item);
            setTitle(item.getTitle());
        }
    }

    @Override
    public Object onPostLogin() throws IOException {
        PhotoDao dao = MobileKKM.getDatabase().photoDao();
        Photo photo = dao.getById(mAccount.getPhotoId());

        // Check if photo isn't null, otherwise fetch from website
        if (photo == null || photo.getBitmap() == null) {
            photo = MobileKKM.getLoginHelper().getPhoto(mAccount);
            dao.insert(photo);
        }

        return photo;
    }

    @Override
    public void onSuccess(Object result) {
        Photo photo = (Photo) result;

        // Set photo as drawerBackground, TODO: check if photoId has changed and fetch photo then only
        ImageView drawerBackground = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.drawer_header_background);
        PicassoDrawable drawable = new PicassoDrawable(MainActivity.this, photo.getBitmap(), drawerBackground.getDrawable(), false);
        drawerBackground.setImageDrawable(drawable);

        // First inject session data into webview local storage, then load the webapp
        String inject = "<script type='text/javascript'>" +
                "localStorage.setItem('fingerprint', '" + MobileKKM.getLoginHelper().getFingerprint() + "');" +
                "localStorage.setItem('token', '" + MobileKKM.getLoginHelper().getSessionToken() + "');" +
                "window.location.replace('https://m.kkm.krakow.pl/#!/home');" +
                "</script>";
        mWebview.loadDataWithBaseURL("https://m.kkm.krakow.pl/injeScct", inject, "text/html", "utf-8", null);
        mAuthTask = null;
    }

    @Override
    public void onError(int errorCode, String message) {
        mAuthTask = null;

        if (errorCode == Const.ErrorCode.LOGIN_ERROR) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            logout();
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
