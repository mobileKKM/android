package de.codebucket.mkkm.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;

import com.google.android.material.snackbar.Snackbar;
import com.takisoft.preferencex.PreferenceFragmentCompat;

import org.json.JSONException;
import org.json.JSONObject;

import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.R;
import de.codebucket.mkkm.login.AccountUtils;
import de.codebucket.mkkm.login.LoginHelper;
import de.codebucket.mkkm.util.FileHelper;

public class BackupActivity extends ToolbarActivity {

    private static final int OPEN_FILE_RESULT_CODE = 98;
    private static final int SAVE_FILE_RESULT_CODE = 99;

    private static final int REQUEST_READ_PERMISSION_CODE = 198;
    private static final int REQUEST_WRITE_PERMISSION_CODE = 199;

    private static final String RESTORE_TUTORIAL_URL = "https://goo.gl/gisUoy";

    private View mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        // Set up action bar
        setupToolbar();
        setTitle(R.string.title_activity_backup);

        ViewStub stub = findViewById(R.id.container_stub);
        mContainer = stub.inflate();

        if (savedInstanceState == null) {
            // Display the fragment as the main content.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_content, new BackupFragment())
                    .commit();
        }

        // Enable back arrow button to return
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Ignore if the result isn't OK or data intent is null
        if (resultCode != Activity.RESULT_OK || intent == null) {
            super.onActivityResult(requestCode, resultCode, intent);
            return;
        }

        // Handle file chooser
        switch (requestCode) {
            case OPEN_FILE_RESULT_CODE:
                doRestore(intent.getData());
                return;
            case SAVE_FILE_RESULT_CODE:
                doBackup(intent.getData());
                return;
            default:
                break;
        }
    }

    private void showOpenFileSelector() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        startActivityForResult(intent, OPEN_FILE_RESULT_CODE);
    }

    private void showSaveFileSelector() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        intent.putExtra(Intent.EXTRA_TITLE, FileHelper.generateBackupFilename());
        startActivityForResult(intent, SAVE_FILE_RESULT_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_PERMISSION_CODE) {
            // Check if user has granted read permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showOpenFileSelector();
            } else {
                // Show error about missing permissions
                Snackbar.make(mContainer, R.string.backup_permissions_not_granted, Snackbar.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_WRITE_PERMISSION_CODE) {
            // Check if user has granted read permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSaveFileSelector();
            } else {
                // Show error about missing permissions
                Snackbar.make(mContainer, R.string.backup_permissions_not_granted, Snackbar.LENGTH_LONG).show();
            }
        } else {
            // Handle it elsewhere lol
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void openFileWithPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            showOpenFileSelector();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_READ_PERMISSION_CODE);
        }
    }

    public void saveFileWithPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            showSaveFileSelector();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_WRITE_PERMISSION_CODE);
        }
    }

    private void doRestore(Uri uri) {
        if (FileHelper.isExternalStorageReadable()) {
            try {
                JSONObject json = new JSONObject(FileHelper.readFileToString(this, uri));
                String account = json.getString("account");
                String fingerprint = json.getString("fingerprint");

                // Check if associated account is currently logged in
                if (AccountUtils.getPassengerId(AccountUtils.getAccount()).equals(account)) {
                    MobileKKM.getLoginHelper().updateFingerprint(fingerprint);
                    Toast.makeText(this, R.string.backup_import_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.backup_wrong_account, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException ex) {
                // Something unknown went wrong
                Toast.makeText(this, R.string.backup_json_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show error about missing permissions
            Toast.makeText(this, R.string.backup_permissions_not_granted, Toast.LENGTH_SHORT).show();
        }
    }

    private void doBackup(Uri uri) {
        if (FileHelper.isExternalStorageWritable()) {
            try {
                JSONObject json = new JSONObject();
                json.put("account", AccountUtils.getPassengerId(AccountUtils.getAccount()));
                json.put("fingerprint", MobileKKM.getLoginHelper().getFingerprint());

                // Save backup to file and show response
                if (FileHelper.writeStringToFile(this, uri, json.toString())) {
                    Toast.makeText(this, R.string.backup_export_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.backup_export_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException ex) {
                // Something unknown went wrong
                Toast.makeText(this, R.string.backup_json_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show error about missing permissions
            Toast.makeText(this, R.string.backup_permissions_not_granted, Toast.LENGTH_SHORT).show();
        }
    }

    public void showRestoreDialog() {
        int marginSmall = getResources().getDimensionPixelSize(R.dimen.activity_margin_small);
        int marginMedium = getResources().getDimensionPixelSize(R.dimen.activity_margin_medium);

        final EditText input = new EditText(this);
        input.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        input.setTypeface(Typeface.MONOSPACE);
        input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        input.setSingleLine();

        FrameLayout container = new FrameLayout(this);
        container.setPaddingRelative(marginMedium, marginSmall, marginMedium, 0);
        container.addView(input);

        new AlertDialog.Builder(this)
                .setTitle(R.string.backup_restore_fingerprint)
                .setView(container)
                .setNegativeButton(R.string.dialog_cancel, null)
                .setNeutralButton(R.string.dialog_help, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            // Open website with privacy policy
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(RESTORE_TUTORIAL_URL));
                            startActivity(intent);
                        } catch (ActivityNotFoundException exc) {
                            // Believe me, this actually happens.
                            Toast.makeText(BackupActivity.this, R.string.no_browser_activity, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String fingerprint = input.getText().toString();

                        // Check if entered fingerprint is a valid uuid
                        if (!LoginHelper.isValidUUID(fingerprint)) {
                            Toast.makeText(BackupActivity.this, R.string.backup_invalid_fingerprint, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        MobileKKM.getLoginHelper().updateFingerprint(fingerprint);
                        Toast.makeText(BackupActivity.this, R.string.backup_import_success, Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    public static class BackupFragment extends PreferenceFragmentCompat {

        private BackupActivity mContext;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mContext = (BackupActivity) getActivity();

            // Import options
            Preference backupImport = findPreference("backup_import");
            Preference backupRestore = findPreference("backup_restore");

            backupImport.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    mContext.openFileWithPermissions();
                    return true;
                }
            });

            backupRestore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    mContext.showRestoreDialog();
                    return true;
                }
            });

            // Export options
            Preference backupExport = findPreference("backup_export");

            backupExport.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    mContext.saveFileWithPermissions();
                    return true;
                }
            });
        }

        @Override
        public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preferences_backup);
        }
    }
}
