package de.codebucket.mkkm;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;

import java.util.UUID;

import de.codebucket.mkkm.util.LooperExecutor;
import de.codebucket.mkkm.util.RuntimeHelper;

public class MobileKKM extends Application {

    private static final String TAG = "MobileKKM";

    private static MobileKKM instance;
    private static SharedPreferences preferences;

    private static final HandlerThread sWorkerThread = new HandlerThread("loader");
    private static final long WAIT_BEFORE_RESTART = 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Use Android Device ID as fingerprint
        // mKKM webapp uses fingerprint2.js to generate a fingerprint based on user-agent
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (preferences.getString("fingerprint", null) == null) {
            preferences.edit().putString("fingerprint", getFingerprint()).apply();
        }

        sWorkerThread.start();
    }

    public String getFingerprint() {
        String deviceId = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        return UUID.nameUUIDFromBytes(deviceId.getBytes()).toString().replaceAll("-", "");
    }

    public boolean isNetworkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isConnected();
            }
        }

        return false;
    }

    public static MobileKKM getInstance() {
        return instance;
    }

    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public static void restartApp(final Context context) {
        ProgressDialog.show(context, null, context.getString(R.string.state_loading), true, false);
        new LooperExecutor(sWorkerThread.getLooper()).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(WAIT_BEFORE_RESTART);
                } catch (Exception ex) {
                    Log.e(TAG, "Error waiting", ex);
                }

                RuntimeHelper.triggerRestart(context);
            }
        });
    }

    public static boolean isDebug() {
        return BuildConfig.DEBUG && BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug");
    }
}
