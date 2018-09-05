package de.codebucket.mkkm;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;

public class MobileKKM extends Application {

    public static final String TAG = "MobileKKM";

    private static MobileKKM instance;
    private static SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Use Android Device ID as fingerprint
        // mKKM webapp uses fingerprint2.js to generate a fingerprint based on user-agent
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (preferences.getString("fingerprint", "").isEmpty()) {
            String fingerprint = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
            preferences.edit().putString("fingerprint", fingerprint).apply();
        }
    }

    public static MobileKKM getInstance() {
        return instance;
    }

    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public static String getFingerprint() {
        return preferences.getString("fingerprint", null);
    }
}
