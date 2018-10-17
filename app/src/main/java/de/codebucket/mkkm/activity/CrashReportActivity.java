package de.codebucket.mkkm.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

import de.codebucket.mkkm.R;

public class CrashReportActivity extends ToolbarActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);

        // Set up action bar
        setupToolbar();
        setTitle(R.string.title_activity_crash);

        TextView stacktrace = findViewById(R.id.stacktrace);
        stacktrace.setText(CustomActivityOnCrash.getStackTraceFromIntent(getIntent()));
    }
}
