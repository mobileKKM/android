package de.codebucket.mkkm;

import android.webkit.JavascriptInterface;

public class LoginHelper {

    private String mUsername;
    private String mPassword;

    public LoginHelper(String username, String password) {
        mUsername = username;
        mPassword = password;
    }

    @JavascriptInterface
    public String getUsername() {
        return mUsername;
    }

    @JavascriptInterface
    public String getPassword() {
        return mPassword;
    }
}