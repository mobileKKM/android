package de.codebucket.mkkm.activity;

import android.os.Bundle;

import de.codebucket.mkkm.R;

public class BackupActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        // Set up action bar
        setupToolbar();
        setTitle(R.string.title_activity_backup);

        // Enable back arrow button to return
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
