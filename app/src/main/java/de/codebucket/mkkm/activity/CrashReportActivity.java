package de.codebucket.mkkm.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import de.codebucket.mkkm.R;

public class CrashReportActivity extends ToolbarActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);

        // Set up action bar
        setupToolbar();
        setTitle(R.string.title_activity_crash);
    }
}
