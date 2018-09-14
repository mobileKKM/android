package de.codebucket.mkkm.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import de.codebucket.mkkm.BuildConfig;
import de.codebucket.mkkm.R;
import de.codebucket.mkkm.KKMWebviewClient;
import de.codebucket.mkkm.login.AuthenticatorService;
import de.codebucket.mkkm.login.SessionProfile;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Main";
    private static final int TIME_INTERVAL = 2000;

    private WebView mWebview;
    private long mBackPressed;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up drawer menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        setTitle(navigationView.getMenu().getItem(0).getTitle());

        // Get user session profile from login
        final SessionProfile profile = (SessionProfile) getIntent().getSerializableExtra("profile");

        View headerView = navigationView.getHeaderView(0);
        TextView drawerUsername = (TextView) headerView.findViewById(R.id.drawer_username);
        drawerUsername.setText(String.format("%s %s", profile.getFirstName(), profile.getLastName()));

        TextView drawerEmail = (TextView) headerView.findViewById(R.id.drawer_email);
        drawerEmail.setText(profile.getEmailAddress());

        ImageView drawerBackground = (ImageView) headerView.findViewById(R.id.drawer_header_background);
        profile.loadPhoto(drawerBackground);

        // Load webview layout and disable "refreshing"
        SwipeRefreshLayout swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setRefreshing(false);
        swipe.setEnabled(false);

        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mWebview = (WebView) findViewById(R.id.webview);
        mWebview.setWebViewClient(new KKMWebviewClient(this));
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setDomStorageEnabled(true);
        mWebview.getSettings().setAppCacheEnabled(true);
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        // First inject session data into webview local storage, then load the webapp
        String inject = "<script type='text/javascript'>" +
                            "localStorage.setItem('fingerprint', '" + profile.getFingerprint() + "');" +
                            "localStorage.setItem('token', '" + profile.getToken() + "');" +
                            "window.location.replace('https://m.kkm.krakow.pl/#!/home');" +
                        "</script>";
        mWebview.loadDataWithBaseURL("https://m.kkm.krakow.pl/inject", inject, "text/html", "utf-8", null);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
                return false;
            case R.id.nav_logout:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_logout_title)
                        .setMessage(R.string.dialog_logout_warning)
                        .setNegativeButton(R.string.dialog_no, null)
                        .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AccountManager accountManager = AccountManager.get(MainActivity.this);
                                Account account = AuthenticatorService.getUserAccount(MainActivity.this);
                                accountManager.removeAccount(account, null, null);

                                // Return back to login screen
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();
                            }
                        })
                        .show();
                return false;
        }

        // Change title only on checkable items
        if (item.isCheckable()) {
            setTitle(item.getTitle());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
