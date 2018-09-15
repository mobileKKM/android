package de.codebucket.mkkm;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;

import java.util.UUID;

public class MobileKKM extends Application {

    private static final String TAG = "MobileKKM";

    private static MobileKKM instance;
    private static SharedPreferences preferences;

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
    }

    public String getFingerprint() {
        String deviceId =  Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
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

    public static boolean isDebug() {
        return BuildConfig.DEBUG && BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug");
    }
}
