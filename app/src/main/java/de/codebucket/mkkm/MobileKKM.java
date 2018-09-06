package de.codebucket.mkkm;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;

import java.io.File;

import de.codebucket.mkkm.webview.UserProfileStorage;

public class MobileKKM extends Application {

    public static final String TAG = "MobileKKM";

    private static MobileKKM instance;
    private static SharedPreferences preferences;
    private static UserProfileStorage userprofile;

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

        // Check if userprofile.json exists
        File json = new File(getApplicationContext().getFilesDir(), "userprofile.json");
        if (!json.exists()) {
            UserProfileStorage.init(json);
        }

        userprofile = new UserProfileStorage(json);

        // TODO: remove migration
        if (!preferences.getString("fingerprint", "").isEmpty()) {
            userprofile.setItem("fingerprint", preferences.getString("fingerprint", ""));
            userprofile.setItem("token", preferences.getString("token", ""));
            userprofile.setItem("user", preferences.getString("user", ""));
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

    public static void restartApplication() {

    }

    public static String login(String username, String password) {
        return null;
    }
}
