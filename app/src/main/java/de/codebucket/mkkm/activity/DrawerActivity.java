package de.codebucket.mkkm.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import de.codebucket.mkkm.KKMWebViewClient;
import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.R;
import de.codebucket.mkkm.database.model.Account;
import de.codebucket.mkkm.login.AccountUtils;

import static de.codebucket.mkkm.KKMWebViewClient.getPageUrl;

public abstract class DrawerActivity extends WebViewActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int TIME_INTERVAL = 2000;

    protected NavigationView mNavigationView;
    protected long mBackPressed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setupDrawer() {
        // Set up drawer menu
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(0).setChecked(true);

        // Set title to first navbar item
        setTitle(mNavigationView.getMenu().getItem(0).getTitle());
    }

    public void setupHeaderView(Account account) {
        View headerView = mNavigationView.getHeaderView(0);

        TextView headerNameView = headerView.findViewById(R.id.drawer_header_name);
        headerNameView.setText(getString(R.string.nav_header_title, account.getFirstName(), account.getLastName()));

        TextView headerEmailView = headerView.findViewById(R.id.drawer_header_email);
        headerEmailView.setText(getString(R.string.nav_header_subtitle, account.getEmail()));

        ViewCompat.setOnApplyWindowInsetsListener(headerView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v.getLayoutParams();
                params.height = getResources().getDimensionPixelSize(R.dimen.nav_header_height) + insets.getSystemWindowInsetTop();
                v.setLayoutParams(params);

                // Get header padding from dimens.xml in pixels
                int paddingSize = getResources().getDimensionPixelSize(R.dimen.nav_header_standard_padding);

                View relative = v.findViewById(R.id.drawer_header_relative);
                relative.setPadding(
                        relative.getPaddingLeft(),
                        paddingSize + insets.getSystemWindowInsetTop(),
                        relative.getPaddingRight(),
                        relative.getPaddingBottom()
                );

                return insets;
            }
        });
    }

    public void setupHeaderAvatar(final Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView headerAvatarView = mNavigationView.getHeaderView(0).findViewById(R.id.drawer_header_avatar);
                headerAvatarView.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        // Check if current page is not home
        String homeUrl = getPageUrl("home");
        if (mWebView.getUrl() != null && !(mWebView.getUrl().equals(homeUrl) || mWebView.getUrl().equals("about:blank"))) {
            mWebView.loadUrl(homeUrl);
            return;
        }

        // Press back to exit twice
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            mWebView.reload();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_tickets:
                mWebView.loadUrl(getPageUrl("home")); // TODO: Replace with TicketOverviewFragment
                break;
            case R.id.nav_purchase:
                mWebView.loadUrl(getPageUrl("ticket/buy")); // TODO: Add custom webview handler for purchasing
                break;
            case R.id.nav_account:
                mWebView.loadUrl(getPageUrl("account")); // TODO: Replace with UserAccountFragment
                break;
            case R.id.nav_citizen_status:
                mWebView.loadUrl(getPageUrl("citizen-status"));
                break;
            case R.id.nav_pricing:
                mWebView.loadUrl("https://www.codebucket.de/mobilekkm/cennik.html");
                break;
            case R.id.nav_backup:
                startActivity(new Intent(DrawerActivity.this, BackupActivity.class));
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
        MenuItem item;

        switch (page) {
            case KKMWebViewClient.PAGE_HOME:
            case KKMWebViewClient.PAGE_CONTROL:
                item = mNavigationView.getMenu().findItem(R.id.nav_tickets);
                break;
            case KKMWebViewClient.PAGE_PURCHASE:
                item = mNavigationView.getMenu().findItem(R.id.nav_purchase);
                break;
            case KKMWebViewClient.PAGE_ACCOUNT:
                item = mNavigationView.getMenu().findItem(R.id.nav_account);
                break;
            case KKMWebViewClient.PAGE_CITIZEN_STATUS:
                item = mNavigationView.getMenu().findItem(R.id.nav_citizen_status);
                break;
            default:
                return;
        }

        // Set different title for ticket control
        if (KKMWebViewClient.PAGE_CONTROL.equals(page)) {
            setTitle(R.string.nav_control);
            return;
        }

        // Check if item is not checked or title is different than item's
        if (!item.isChecked() || !getTitle().equals(item.getTitle())) {
            mNavigationView.setCheckedItem(item);
            setTitle(item.getTitle());
        }

        // Show payment reminder dialog before purchasing new ticket
        final SharedPreferences prefs = MobileKKM.getPreferences();
        if (KKMWebViewClient.PAGE_PURCHASE.equals(page) && prefs.contains("last_payment_url")) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(R.string.dialog_payment_reminder_title)
                    .setMessage(R.string.dialog_payment_reminder_body)
                    .setNeutralButton(R.string.dialog_cancel, null)
                    .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            prefs.edit().remove("last_payment_url").apply();
                        }
                    })
                    .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mWebView.loadUrl(prefs.getString("last_payment_url", null));
                        }
                    })
                    .show();
        }
    }

    protected void doLogout() {
        AccountUtils.removeAccount(AccountUtils.getAccount());

        // Remove saved payment if there is ongoing
        SharedPreferences prefs = MobileKKM.getPreferences();
        if (prefs.contains("last_payment_url")) {
            prefs.edit().remove("last_payment_url").apply();
        }

        // Return back to login screen
        startActivity(new Intent(DrawerActivity.this, LoginActivity.class));
        finish();
    }
}
