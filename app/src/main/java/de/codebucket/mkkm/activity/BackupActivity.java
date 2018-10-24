package de.codebucket.mkkm.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;

import com.google.android.material.snackbar.Snackbar;
import com.takisoft.preferencex.PreferenceFragmentCompat;

import de.codebucket.mkkm.R;
import de.codebucket.mkkm.util.FileHelper;

public class BackupActivity extends ToolbarActivity {

    private static final int OPEN_FILE_RESULT_CODE = 98;
    private static final int SAVE_FILE_RESULT_CODE = 99;

    private static final int REQUEST_READ_PERMISSION_CODE = 198;
    private static final int REQUEST_WRITE_PERMISSION_CODE = 199;


    private View mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        // Set up action bar
        setupToolbar();
        setTitle(R.string.title_activity_backup);

        ViewStub stub = findViewById(R.id.container_stub);
        stub.inflate();

        mContainer = stub.getRootView();

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_READ_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showOpenFileSelector();
            } else {
                Snackbar.make(mContainer, R.string.backup_storage_unreadable, Snackbar.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_WRITE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSaveFileSelector();
            } else {
                Snackbar.make(mContainer, R.string.backup_storage_unwritable, Snackbar.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void openFileWithPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            showOpenFileSelector();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_READ_PERMISSION_CODE);
        }
    }

    private void saveFileWithPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            showSaveFileSelector();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_WRITE_PERMISSION_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    public static class BackupFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Import options
            Preference backupImport = findPreference("backup_import");
            Preference backupRestore = findPreference("backup_restore");

            backupImport.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ((BackupActivity) getActivity()).openFileWithPermissions();
                    return true;
                }
            });

            // Export options
            Preference backupExport = findPreference("backup_export");

            backupExport.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ((BackupActivity) getActivity()).saveFileWithPermissions();
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
