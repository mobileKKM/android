package de.codebucket.mkkm.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

import de.codebucket.mkkm.BuildConfig;
import de.codebucket.mkkm.R;

public class CrashReportActivity extends ToolbarActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);

        // Set up action bar
        setupToolbar();
        setTitle(R.string.title_activity_crash);

        TextView crashReport = findViewById(R.id.crash_report);
        String report = getString(R.string.crash_apologise) +  ":( \n";
        report += "-------------------------------------\n";
        report += createErrorReport(getIntent());
        crashReport.setText(report);
    }

    private String createErrorReport(Intent intent) {
        String versionName = BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")";
        String details = "";

        details += "mobileKKM bug report " + SimpleDateFormat.getDateTimeInstance().format(new Date()) + "\n";
        details += "\n";
        details += "Build version: " + versionName + "\n";
        details += "Device: " + Build.MODEL + " (" + Build.DEVICE + ") " + "[" + Build.FINGERPRINT + "]\n";
        details += "\n";

        details += "build.brand=" + Build.BRAND + "\n";
        details += "build.device=" + Build.DEVICE + "\n";
        details += "build.display=" + Build.DISPLAY + "\n";
        details += "build.fingerprint=" + Build.FINGERPRINT + "\n";
        details += "build.hardware=" + Build.HARDWARE + "\n";
        details += "build.id=" + Build.ID + "\n";
        details += "build.manufacturer=" + Build.MANUFACTURER + "\n";
        details += "build.model=" + Build.MODEL + "\n";
        details += "build.product=" + Build.PRODUCT + "\n";
        details += "build.type=" + Build.TYPE + "\n";
        details += "version.codename=" + Build.VERSION.CODENAME + "\n";
        details += "version.incremental=" + Build.VERSION.INCREMENTAL + "\n";
        details += "version.release=" + Build.VERSION.RELEASE + "\n";
        details += "version.sdk_int=" + Build.VERSION.SDK_INT + "\n";
        details += "\n";

        details += "--------- beginning of stacktrace\n";
        details += CustomActivityOnCrash.getStackTraceFromIntent(intent);
        return details;

    }
}
