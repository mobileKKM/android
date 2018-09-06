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

        // Check if userprofile.json exists
        File json = new File(getApplicationContext().getFilesDir(), "userprofile.json");
        if (!json.exists()) {
            UserProfileStorage.init(json);
        }

        // Use Android Device ID as fingerprint
        // mKKM webapp uses fingerprint2.js to generate a fingerprint based on user-agent
        userprofile = new UserProfileStorage(json);
        if (userprofile.getItem("fingerprint") == null) {
            userprofile.setItem("fingerprint", getFingerprint());
        }
    }

    public String getFingerprint() {
        return Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
    }

    public static MobileKKM getInstance() {
        return instance;
    }

    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public static UserProfileStorage getUserProfile() {
        return userprofile;
    }

    public static void restartApplication() {

    }

    public static String login(String username, String password) {
        return null;
    }
}
