package de.codebucket.mkkm.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.R;

public class Const {

    public static String FACEBOOK_URL = "https://www.facebook.com/getmobilekkm";
    public static String FACEBOOK_PAGE_ID = "496708800811072";

    // method to get the right URL to use in the intent
    public static String getFacebookPageUrl(Context context) {
        PackageManager packageManager = context.getPackageManager();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo("com.facebook.katana", PackageManager.GET_ACTIVITIES);
            if (packageInfo.applicationInfo.enabled) {
                // app is installed and enabled
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException ignored) {}

        // return normal web url if app is not installed or disabled
        return FACEBOOK_URL;
    }

    public static final class ErrorCode {
        public static final int SUCCESS = 0;
        public static final int NO_NETWORK = 1;
        public static final int NO_ACCOUNT = 2;
        public static final int INVALID_FINGERPRINT = 3;
        public static final int INVALID_CREDENTIALS = 4;
        public static final int CONNECTION_ERROR = 5;
        public static final int SERVER_ERROR = 6;
        public static final int WRONG_RESPONSE = 7;
        public static final int LOGIN_ERROR = 8;
        public static final int UNKNOWN = 9;
    }

    public static String getErrorMessage(int errorCode, String fallback) {
        MobileKKM app = MobileKKM.getInstance();
        switch (errorCode) {
            case ErrorCode.NO_NETWORK:
            case ErrorCode.CONNECTION_ERROR:
                return app.getString(R.string.error_no_network);
            case ErrorCode.NO_ACCOUNT:
                return app.getString(R.string.error_account);
            case ErrorCode.INVALID_FINGERPRINT:
                return app.getString(R.string.error_fingerprint);
            case ErrorCode.WRONG_RESPONSE:
            case ErrorCode.UNKNOWN:
                return app.getString(R.string.error_unknown);
            default:
                return fallback;
        }
    }
}
