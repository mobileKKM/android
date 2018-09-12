package de.codebucket.mkkm.ui;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;

import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.R;
import de.codebucket.mkkm.login.SessionProfile;
import de.codebucket.mkkm.login.LoginFailedException;
import de.codebucket.mkkm.login.LoginFailedException.ErrorType;

import static android.Manifest.permission.READ_CONTACTS;
import static android.util.Patterns.EMAIL_ADDRESS;

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    public static final String TAG = "Login";

    // Login stuff
    private static final int REQUEST_READ_CONTACTS = 0;
    private UserLoginTask mAuthTask = null;

    // UI references.
    private ProgressDialog mProgressDialog;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button mLoginButton;
    private View mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if user is already signed in
        // TODO: Check if account is stored on device, read username and password to perform login

        setTitle(R.string.title_activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.input_email);
        populateAutoComplete();

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
                openWebsite(Uri.parse("https://m.kkm.krakow.pl/#!/register"));
            }
        });

        mScrollView = (ScrollView) findViewById(R.id.scroll_view);
    }

    /**
     * Request permission to read contacts
     */

    private void populateAutoComplete() {
        // Don't init anything if no permission granted
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        // Lol no need on Lollipop or lower, or if permission granted
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        // Request permission and return false for now
        if (!shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            requestPermissions(new String[]{ READ_CONTACTS }, REQUEST_READ_CONTACTS);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Repeat process if permission has been granted
                populateAutoComplete();
            }
        }
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

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute();
        }
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
            builder.setToolbarColor(getResources().getColor(R.color.primary));
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
            mProgressDialog = ProgressDialog.show(this, null, getString(R.string.dialog_login_msg), true, false);
            mLoginButton.setEnabled(false);
        } else {
            mProgressDialog.dismiss();
            mLoginButton.setEnabled(true);
        }
    }

    private void showError(String errorMessage) {
        Snackbar.make(mScrollView, errorMessage, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Read e-mail addresses from contacts after contacts permission has been granted
     */

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Execute user auth asynchronously in background
     */

    public class UserLoginTask extends AsyncTask<Void, Void, SessionProfile> {

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
        protected SessionProfile doInBackground(Void... params) {
            String token = null;

            try {
                token = MobileKKM.getLoginHelper().getToken(mEmail, mPassword);
            } catch (LoginFailedException ex) {
                exception = ex;
                return null;
            }

            return MobileKKM.getLoginHelper().getSession(token);
        }

        @Override
        protected void onPostExecute(final SessionProfile profile) {
            mAuthTask = null;
            showProgress(false);

            if (profile != null) {
                // TODO: Save account on device

                // Open MainActivity with signed in user
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("profile", profile);
                startActivity(intent);
                finish();
            } else if (exception != null) {
                // Print error to log and show message to the user
                // Show a Toast if unknown error
                Log.e(TAG, String.format("%s: %s", exception.getErrorType(), exception.getErrorMessage()));
                if (exception.getErrorType() != ErrorType.UNKNOWN) {
                    showError(exception.getErrorMessage());
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

