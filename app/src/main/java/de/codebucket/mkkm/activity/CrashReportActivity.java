package de.codebucket.mkkm.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AlertDialog;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

import de.codebucket.mkkm.BuildConfig;
import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.R;

public class CrashReportActivity extends ToolbarActivity {

    public static final String REPORT_EMAIL_ADDRESS = "projects@codebucket.de";
    public static final String REPORT_EMAIL_SUBJECT = "Błąd w mobileKKM " + BuildConfig.VERSION_NAME;

    private String mCrashReport;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);

        // Set up action bar
        setupToolbar();
        setTitle(R.string.title_activity_crash);

        // Create crash report from stacktrace
        mCrashReport = createErrorReport(getIntent());

        // Restart app on cancel
        Button cancelButton = findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobileKKM.restartApp(CrashReportActivity.this);
            }
        });

        // Send crash report to developer
        Button sendButton = findViewById(R.id.btn_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(CrashReportActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.privacy_policy_title)
                        .setMessage(R.string.privacy_policy_body)
                        .setCancelable(false)
                        .setNeutralButton(R.string.read_privacy_policy, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.codebucket.de/privacy-policy.html"));
                                startActivity(webIntent);
                            }
                        })
                        .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:" + REPORT_EMAIL_ADDRESS))
                                        .putExtra(Intent.EXTRA_SUBJECT, REPORT_EMAIL_SUBJECT)
                                        .putExtra(Intent.EXTRA_TEXT, mCrashReport);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(Intent.createChooser(intent, "Send Email"));
                            }
                        })
                        .setNegativeButton(R.string.decline, null)
                        .show();
            }
        });

        // Set report to textview
        TextView errorView = findViewById(R.id.crash_error);
        String report = getString(R.string.crash_apologise) +  ":( \n";
        report += "-------------------------------------\n";
        report += mCrashReport;
        errorView.setText(report);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {

            return true;
        }

        return super.onOptionsItemSelected(item);
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
