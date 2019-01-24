package de.codebucket.mkkm.activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
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
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;

import de.codebucket.mkkm.BuildConfig;
import de.codebucket.mkkm.R;

public class CrashReportActivity extends ToolbarActivity {

    public static final String REPORT_EMAIL_ADDRESS = "mobilekkm@codebucket.de";
    public static final String REPORT_EMAIL_SUBJECT = "Błąd w mobileKKM " + BuildConfig.VERSION_NAME;

    // Always enforce proper date localization
    private static final DateFormat LOCAL_DATEFORMAT = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.forLanguageTag("pl-PL"));

    private String mStacktrace;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);

        // Set up action bar
        setupToolbar();
        setTitle(R.string.title_activity_crash);

        mStacktrace = CustomActivityOnCrash.getStackTraceFromIntent(getIntent());

        // Create crash report from stacktrace
        final CaocConfig caocConfig = CustomActivityOnCrash.getConfigFromIntent(getIntent());
        final String crashReport = createErrorReport(mStacktrace);

        // Restart app on cancel
        Button cancelButton = findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CrashReportActivity.this, SplashActivity.class);
                CustomActivityOnCrash.restartApplicationWithIntent(CrashReportActivity.this, intent, caocConfig);
            }
        });

        // Send crash report to developer
        Button sendButton = findViewById(R.id.btn_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(CrashReportActivity.this)
                        .setTitle(R.string.privacy_policy_title)
                        .setMessage(R.string.privacy_policy_body)
                        .setCancelable(false)
                        .setNeutralButton(R.string.read_privacy_policy, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    // Open website with privacy policy
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.codebucket.de/mobilekkm/privacy-policy.html"));
                                    startActivity(intent);
                                } catch (ActivityNotFoundException exc) {
                                    // Believe me, this actually happens.
                                    Toast.makeText(CrashReportActivity.this, R.string.no_browser_activity, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + REPORT_EMAIL_ADDRESS));
                                intent.putExtra(Intent.EXTRA_SUBJECT, REPORT_EMAIL_SUBJECT);
                                intent.putExtra(Intent.EXTRA_TEXT, crashReport);
                                startActivity(Intent.createChooser(intent, getString(R.string.intent_chooser_email)));
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
        report += crashReport;
        errorView.setText(report);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, mStacktrace);
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, getString(R.string.intent_chooser_share)));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String createErrorReport(String stacktrace) {
        String versionName = BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")";
        String details = "";

        details += "mobileKKM bug report " + LOCAL_DATEFORMAT.format(new Date()) + "\n";
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
        details += stacktrace;
        return details;
    }
}
