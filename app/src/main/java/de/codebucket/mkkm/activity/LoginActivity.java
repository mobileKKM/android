package de.codebucket.mkkm.activity;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;

import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.R;
import de.codebucket.mkkm.database.model.Ticket;
import de.codebucket.mkkm.database.model.TicketDao;
import de.codebucket.mkkm.login.AccountUtils;
import de.codebucket.mkkm.login.LoginFailedException;
import de.codebucket.mkkm.login.LoginFailedException.ErrorType;
import de.codebucket.mkkm.login.LoginHelper;
import de.codebucket.mkkm.util.EncryptUtils;

import static android.util.Patterns.EMAIL_ADDRESS;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Login";
    private static final int REGISTRATION_RESULT_CODE = 99;

    // Login stuff
    private UserLoginTask mAuthTask;
    private Account mAccount;

    // UI references
    private ProgressDialog mProgressDialog;
    private EditText mEmailView, mPasswordView;
    private Button mLoginButton;
    private View mLoginForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.title_activity_login);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.input_email);
        mPasswordView = (EditText) findViewById(R.id.input_password);
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

        TextView mRegisterLink = (TextView) findViewById(R.id.link_register);
        mRegisterLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(LoginActivity.this, RegistrationActivity.class), REGISTRATION_RESULT_CODE);
            }
        });

        mLoginForm = (ScrollView) findViewById(R.id.login_form);

        // Check if user is already signed in
        mAccount = AccountUtils.getCurrentAccount();

        if (mAccount != null) {
            // Migrate plain password to encrypted credentials
            String password = AccountUtils.getPasswordEncrypted(mAccount);
            if (!EncryptUtils.isBase64(password)) {
                try {
                    String encryptedPassword = EncryptUtils.encrpytString(password);
                    AccountUtils.setPassword(mAccount, encryptedPassword);
                } catch (Exception ex) {
                    Log.e(TAG, "Failed to encrypt existing password: " + ex);
                }
            } else {
                password = EncryptUtils.decryptString(password);
            }

            mEmailView.setText(mAccount.name);
            mPasswordView.setText(password);
            attemptLogin();
        }

        // Show disclaimer if user hasn't seen yet
        final SharedPreferences preferences = MobileKKM.getPreferences();
        if (!preferences.getBoolean("disclaimer_shown", false) || MobileKKM.isDebug()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.disclaimer_title)
                    .setMessage(R.string.disclaimer_body)
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
        getMenuInflater().inflate(R.menu.login, menu);
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

    /**
     * Login form validation and authorization
     */

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
        mAuthTask = new UserLoginTask(email, password);
        mAuthTask.execute();
    }

    private boolean isEmailValid(String email) {
        return EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return !password.isEmpty() && password.length() >= 6;
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

    private void showProgress(boolean show) {
        if (show) {
            mProgressDialog = ProgressDialog.show(this, null, getString(R.string.progress_login), true, false);
            mLoginButton.setEnabled(false);
        } else {
            mProgressDialog.dismiss();
            mLoginButton.setEnabled(true);
        }
    }

    private void showError(String errorMessage) {
        Snackbar.make(mLoginForm, errorMessage, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Execute user auth asynchronously in background
     */

    public class UserLoginTask extends AsyncTask<Void, Void, de.codebucket.mkkm.database.model.Account> {

        private final String mEmail;
        private final String mPassword;

        private LoginFailedException exception;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            // Check if device is connected to the Internet
            if (!MobileKKM.getInstance().isNetworkConnectivity()) {
                showError(getString(R.string.error_no_network));
                cancel(true);
                return;
            }

            // Don't continue if fingerprint is invalid
            if (!MobileKKM.getLoginHelper().isFingerprintValid()) {
                showError(getString(R.string.error_fingerprint));
                cancel(true);
                return;
            }
        }

        @Override
        protected de.codebucket.mkkm.database.model.Account doInBackground(Void... params) {
            LoginHelper loginHelper = MobileKKM.getLoginHelper();

            try {
                loginHelper.login(mEmail, mPassword);
            } catch (LoginFailedException ex) {
                exception = ex;
                return null;
            }

            de.codebucket.mkkm.database.model.Account account = null;

            try {
                account = loginHelper.getAccount();

                // Delete saved tickets
                TicketDao dao = MobileKKM.getDatabase().ticketDao();
                for (Ticket ticket : dao.getAllForPassenger(account.getPassengerId())) {
                    dao.delete(ticket);
                }

                // Fetch new and save
                List<Ticket> tickets = loginHelper.getTickets();
                dao.insertAll(tickets);
            } catch (LoginFailedException ex) {
                exception = ex;
            }

            return account;
        }

        @Override
        protected void onPostExecute(final de.codebucket.mkkm.database.model.Account account) {
            mAuthTask = null;
            showProgress(false);

            if (account != null) {
                // Save account on device if no account
                boolean firstSetup = false;
                if (mAccount == null) {
                    AccountUtils.addAccount(mEmail, mPassword, account.getPassengerId());
                    firstSetup = true;
                }

                // Open MainActivity with signed in user
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("account", account);
                intent.putExtra("firstSetup", firstSetup);
                startActivity(intent);
                finish();
            } else if (exception != null) {
                // Remove account if credentials are wrong
                if (mAccount != null && exception.getErrorType() == ErrorType.BACKEND) {
                    AccountUtils.removeAccount(mAccount);
                }

                // Print error to log and show message to the user
                Log.e(TAG, String.format("%s: %s", exception.getErrorType(), exception.getErrorMessage()));
                showError(exception.getErrorMessage());
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
