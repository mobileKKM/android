package de.codebucket.mkkm.ui;

import android.annotation.SuppressLint;
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

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import de.codebucket.mkkm.BuildConfig;
import de.codebucket.mkkm.R;
import de.codebucket.mkkm.KKMWebviewClient;
import de.codebucket.mkkm.login.SessionProfile;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Main";

    private WebView mWebview;

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
        mWebview.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebview.getSettings().setAllowUniversalAccessFromFileURLs(true);
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
        } else {
            super.onBackPressed();
        }
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
                mWebview.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=https://m.kkm.krakow.pl/instructions/CENNIK.pdf");
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
}
