package de.codebucket.mkkm.login;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.codebucket.mkkm.MobileKKM;

public class LoginHelper {

    private static final String PROFILE_URL = "https://m.kkm.krakow.pl/profile/%s";

    private Context mContext;
    private String mFingerprint;

    public LoginHelper(Context context) {
        mContext = context;
        mFingerprint = MobileKKM.getPreferences().getString("fingerprint", null);
    }

    public String getToken(String username, String password) throws LoginFailedException {
        // Check if device has a network connection to the Internet
        if (!isNetworkConnectivity()) {
            throw new LoginFailedException(LoginError.NETWORK_NOT_AVAILABLE);
        }

        // Check if fingerprint exists, if not abort
        if (mFingerprint == null) {
            throw new LoginFailedException(LoginError.INVALID_FINGERPRINT);
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(getEndpointUrl("login"));

        String token = null;

        try {
            // Insert login credentials into POST
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse response = httpClient.execute(httpPost);
            JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));

            // Check if response has error code (no 200 OK)
            if (response.getStatusLine().getStatusCode() != 200) {
                if (json.has("message") && json.getString("message").equalsIgnoreCase("Problem logowania")) {
                    throw new LoginFailedException(LoginError.WRONG_CREDENTIALS);
                } else {
                    throw new LoginFailedException(LoginError.UNKNOWN_ERROR);
                }
            }

            token = json.getString("token");
        } catch (IOException ex) {
            // An IOException occurs only when the low-level connection failed
            throw new LoginFailedException(LoginError.NETWORK_NOT_AVAILABLE);
        } catch (JSONException ex) {
            // This shouldn't happen at all, unless something went wrong on the server
            throw new LoginFailedException(LoginError.UNKNOWN_ERROR);
        }

        return token;
    }

    public SessionProfile getSession(String token) throws SessionExpiredException {
        // Check if device has a network connection to the Internet
        if (!isNetworkConnectivity()) {
            throw new SessionExpiredException(SessionError.NETWORK_NOT_AVAILABLE);
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(getEndpointUrl("account"));

        SessionProfile profile = null;

        return profile;
    }

    private boolean isNetworkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isConnected();
            }
        }

        return false;
    }

    private String getEndpointUrl(String endpoint) {
        return String.format(PROFILE_URL, mFingerprint) + "/" + endpoint;
    }

    public enum LoginError {
        // No internet connection available
        NETWORK_NOT_AVAILABLE,

        // Fingerprint invalid or missing,
        INVALID_FINGERPRINT,

        // User entered wrong user credentials
        WRONG_CREDENTIALS,

        // Anything else...
        UNKNOWN_ERROR
    }

    public class LoginFailedException extends Exception {
        private LoginError mError;

        LoginFailedException(LoginError error) {
            mError = error;
        }

        public LoginError getError() {
            return mError;
        }
    }

    public enum SessionError {
        // No internet connection available
        NETWORK_NOT_AVAILABLE,

        // Session expired
        SESSION_EXPIRED
    }

    public class SessionExpiredException extends Exception {
        private SessionError mError;

        SessionExpiredException(SessionError error) {
            mError = error;
        }

        public SessionError getError() {
            return mError;
        }
    }
}