package de.codebucket.mkkm.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewStub;

import androidx.fragment.app.FragmentManager;

import com.takisoft.preferencex.PreferenceCategory;
import com.takisoft.preferencex.PreferenceFragmentCompat;

import de.codebucket.mkkm.R;

public class BackupActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set up action bar
        setupToolbar();
        setTitle(R.string.title_activity_backup);

        ViewStub stub = findViewById(R.id.container_stub);
        stub.inflate();

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

    public static class BackupFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preferences_backup);
        }
    }
}
