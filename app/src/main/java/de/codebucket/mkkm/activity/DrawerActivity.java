package de.codebucket.mkkm.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import de.codebucket.mkkm.KKMWebViewClient;
import de.codebucket.mkkm.R;
import de.codebucket.mkkm.database.model.Account;
import de.codebucket.mkkm.login.AccountUtils;
import de.codebucket.mkkm.util.PicassoDrawable;

public abstract class DrawerActivity extends WebViewActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int TIME_INTERVAL = 2000;

    protected NavigationView mNavigationView;
    protected long mBackPressed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setupDrawer(Toolbar toolbar) {
        // Set up drawer menu
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(0).setChecked(true);

        // Set title to first navbar item
        setTitle(mNavigationView.getMenu().getItem(0).getTitle());
    }

    public void setupDrawerHeader(Account account) {
        View headerView = mNavigationView.getHeaderView(0);

        TextView drawerUsername = (TextView) headerView.findViewById(R.id.drawer_header_username);
        drawerUsername.setText(String.format("%s %s", account.getFirstName(), account.getLastName()));

        TextView drawerEmail = (TextView) headerView.findViewById(R.id.drawer_header_email);
        drawerEmail.setText(account.getEmail());
    }

    public void setupDrawerBackground(Bitmap bitmap) {
        ImageView drawerBackground = mNavigationView.getHeaderView(0).findViewById(R.id.drawer_header_background);

        PicassoDrawable drawable = new PicassoDrawable(DrawerActivity.this, bitmap, drawerBackground.getDrawable(), false);
        drawerBackground.setImageDrawable(drawable);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        if (item.getItemId() == R.id.action_refresh) {
            mWebview.reload();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
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
                mWebview.loadUrl("https://www.codebucket.de/mkkm/pricing.php");
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
                                doLogout();
                            }
                        })
                        .show();
                break;
            case R.id.nav_settings:
                startActivity(new Intent(DrawerActivity.this, SettingsActivity.class));
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

    protected void doLogout() {
        AccountUtils.removeAccount(AccountUtils.getAccount());

        // Return back to login screen
        startActivity(new Intent(DrawerActivity.this, LoginActivity.class));
        finish();
    }
}
