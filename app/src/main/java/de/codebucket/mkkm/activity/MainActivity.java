package de.codebucket.mkkm.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import de.codebucket.mkkm.BuildConfig;
import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.R;
import de.codebucket.mkkm.KKMWebviewClient;
import de.codebucket.mkkm.database.model.Account;
import de.codebucket.mkkm.database.model.Photo;
import de.codebucket.mkkm.login.AccountUtils;
import de.codebucket.mkkm.login.UserLoginTask;
import de.codebucket.mkkm.util.Const;
import de.codebucket.mkkm.util.PicassoDrawable;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, KKMWebviewClient.OnPageChangedListener, UserLoginTask.CallbackListener {

    private static final String TAG = "Main";
    private static final int TIME_INTERVAL = 2000;

    private Account mAccount;
    private UserLoginTask mAuthTask;

    private NavigationView mNavigationView;
    private WebView mWebview;
    private long mBackPressed;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.title_activity_main);

        // Create notification channel on Android O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("expiry_notification", getString(R.string.expiry_notification), NotificationManager.IMPORTANCE_HIGH);
            MobileKKM.getInstance().getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        MobileKKM.getInstance().setupTicketService();

        // Set up drawer menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(0).setChecked(true);
        setTitle(mNavigationView.getMenu().getItem(0).getTitle());

        // Get user account from login
        mAccount = (de.codebucket.mkkm.database.model.Account) getIntent().getSerializableExtra("account");

        View headerView = mNavigationView.getHeaderView(0);

        TextView drawerUsername = (TextView) headerView.findViewById(R.id.drawer_header_username);
        drawerUsername.setText(String.format("%s %s", mAccount.getFirstName(), mAccount.getLastName()));

        TextView drawerEmail = (TextView) headerView.findViewById(R.id.drawer_header_email);
        drawerEmail.setText(mAccount.getEmail());

        // Load webview layout
        SwipeRefreshLayout swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setColorSchemeColors(getResources().getColor(R.color.colorAccentFallback));
        swipe.setEnabled(true);
        swipe.setRefreshing(true);

        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mWebview = (WebView) findViewById(R.id.webview);
        mWebview.setWebViewClient(new KKMWebviewClient(this, this));
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setDomStorageEnabled(true);
        mWebview.getSettings().setAppCacheEnabled(true);
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        injectWebapp();
    }

    public void injectWebapp() {
        if (mAuthTask != null) {
            return;
        }

        mAuthTask = new UserLoginTask(this);
        mAuthTask.execute();
    }

    private void logout() {
        AccountUtils.removeAccount(AccountUtils.getCurrentAccount());

        // Return back to login screen
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebview.onResume();

        // Check if token has expired and re-inject
        if (MobileKKM.getLoginHelper().isSessionExpired()) {
            injectWebapp();
        }
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (mWebview.canGoBack()) {
            mWebview.goBack();
            return;
        }

        if (mBackPressed + TIME_INTERVAL < System.currentTimeMillis()) {
            Toast.makeText(this, R.string.press_back_again, Toast.LENGTH_SHORT).show();
            mBackPressed = System.currentTimeMillis();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            mWebview.reload();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            case KKMWebviewClient.PAGE_OVERVIEW:
            case KKMWebviewClient.PAGE_CONTROL:
                item = mNavigationView.getMenu().findItem(R.id.nav_tickets);
                break;
            case KKMWebviewClient.PAGE_PURCHASE:
                item = mNavigationView.getMenu().findItem(R.id.nav_purchase);
                break;
            case KKMWebviewClient.PAGE_ACCOUNT:
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
        final Photo photo = MobileKKM.getLoginHelper().getPhoto(mAccount);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView drawerBackground = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.drawer_header_background);
                PicassoDrawable drawable = new PicassoDrawable(MainActivity.this, photo.getBitmap(), drawerBackground.getDrawable(), false);
                drawerBackground.setImageDrawable(drawable);
            }
        });

        return mAccount;
    }

    @Override
    public void onSuccess(Object result) {
        // First inject session data into webview local storage, then load the webapp
        String inject = "<script type='text/javascript'>" +
                "localStorage.setItem('fingerprint', '" + MobileKKM.getLoginHelper().getFingerprint() + "');" +
                "localStorage.setItem('token', '" + MobileKKM.getLoginHelper().getSessionToken() + "');" +
                "window.location.replace('https://m.kkm.krakow.pl/#!/home');" +
                "</script>";
        mWebview.loadDataWithBaseURL("https://m.kkm.krakow.pl/inject", inject, "text/html", "utf-8", null);
    }

    @Override
    public void onError(int errorCode, String message) {
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

    @Override
    public void onTaskFinish() {
        mAuthTask = null;
    }
}
