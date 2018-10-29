package de.codebucket.mkkm.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.R;
import de.codebucket.mkkm.database.model.Account;
import de.codebucket.mkkm.database.model.AccountDao;
import de.codebucket.mkkm.login.AccountUtils;
import de.codebucket.mkkm.login.UserLoginTask;
import de.codebucket.mkkm.util.Const;

import static android.util.Patterns.EMAIL_ADDRESS;

public class LoginActivity extends ToolbarActivity implements UserLoginTask.OnCallbackListener {

    private static final String TAG = "Login";
    private static final int REGISTRATION_RESULT_CODE = 99;

    // Login stuff
    private AlertDialog mAlertDialog;
    private UserLoginTask mAuthTask;

    // UI references
    private ProgressDialog mProgressDialog;
    private TextInputEditText mEmailView, mPasswordView;
    private Button mLoginButton;
    private View mLoginForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Setup action bar
        setupToolbar();
        setTitle(R.string.title_activity_login);

        // Set up the login form.
        mEmailView = (TextInputEditText) findViewById(R.id.input_email);
        mPasswordView = (TextInputEditText) findViewById(R.id.input_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }

                return false;
            }
        });

        mLoginButton = (Button) findViewById(R.id.btn_login);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        TextView mLoginUsingKkLink = (TextView) findViewById(R.id.link_login_using_kk);
        mLoginUsingKkLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        });

        TextView mRegisterLink = (TextView) findViewById(R.id.link_register);
        mRegisterLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(LoginActivity.this, RegistrationActivity.class), REGISTRATION_RESULT_CODE);
            }
        });

        // Pre-fill login fields if provided
        if (getIntent().hasExtra("username")) {
            mEmailView.setText(getIntent().getStringExtra("username"));
            mPasswordView.setText(getIntent().getStringExtra("password"));
        }

        mLoginForm = (ScrollView) findViewById(R.id.login_form);
        showDisclaimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Prevent leaking view exceptions
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != REGISTRATION_RESULT_CODE) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (!data.getBooleanExtra(RegistrationActivity.EXTRA_REGISTRATION_COMPLETE, false)) {
                return;
            }

            Snackbar.make(mLoginForm, R.string.registration_complete, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.snackbar_open, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);
                                LoginActivity.this.startActivity(intent);
                            } catch (ActivityNotFoundException ex) {
                                Log.e(TAG, "No email client found!");
                                Toast.makeText(LoginActivity.this, R.string.no_email_activity, Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setActionTextColor(Color.YELLOW)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // TODO: replace it with a help dialog
        if (id == R.id.action_help) {
            openWebsite(Uri.parse("https://m.kkm.krakow.pl/instructions/FAQ.pdf"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            mEmailView.requestFocus();
            return;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            mEmailView.requestFocus();
            return;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            mPasswordView.requestFocus();
            return;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            mPasswordView.requestFocus();
            return;
        }

        // Show a progress spinner and perform the user login attempt
        showProgress(true);
        mAuthTask = new UserLoginTask(email, password, this);
        mAuthTask.execute();
    }

    private boolean isEmailValid(String email) {
        return EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return !password.isEmpty() && password.length() >= 6;
    }

    @Override
    public Object onPostLogin(Account account) {
        AccountDao dao = MobileKKM.getDatabase().accountDao();
        dao.insert(account);
        return account;
    }

    @Override
    public void onSuccess(Object result) {
        showProgress(false);

        // Save account on device
        Account account = (Account) result;
        AccountUtils.createAccount(mAuthTask.username, mAuthTask.password, account.getPassengerId());

        // Open MainActivity with signed in user
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("account", account);
        intent.putExtra("firstSetup", true);
        startActivity(intent);
        finish();

        mAuthTask = null;
    }

    @Override
    public void onError(int errorCode, String message) {
        mAuthTask = null;
        showProgress(false);

        // Show error message to the user
        Snackbar.make(mLoginForm, Const.getErrorMessage(errorCode, message), Snackbar.LENGTH_LONG).show();
    }

    private void openWebsite(Uri uri) {
        try {
            // Open link in Chrome Custom Tab
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
            builder.setSecondaryToolbarColor(Color.BLACK);
            builder.setShowTitle(true);

            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(this, uri);
        } catch (ActivityNotFoundException ex) {
            Log.e(TAG, "No browser found!");
            Toast.makeText(this, R.string.no_browser_activity, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDisclaimer() {
        // Show disclaimer if user hasn't seen yet
        final SharedPreferences preferences = MobileKKM.getPreferences();
        if (!preferences.getBoolean("disclaimer_shown", false) || MobileKKM.isDebug()) {
            mAlertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.disclaimer_title)
                    .setMessage(R.string.disclaimer_body)
                    .setCancelable(false)
                    .setNegativeButton(R.string.dialog_dont_show_again, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Don't display disclaimer anymore
                            preferences.edit().putBoolean("disclaimer_shown", true).apply();
                        }
                    })
                    .setPositiveButton(R.string.dialog_close, null)
                    .show();
        }
    }

    private void showProgress(boolean show) {
        mLoginButton.setEnabled(!show);
        if (show) {
            mProgressDialog = ProgressDialog.show(this, null, getString(R.string.progress_login), true, false);
        } else {
            mProgressDialog.dismiss();
        }
    }
}
