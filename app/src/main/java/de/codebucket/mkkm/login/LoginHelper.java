package de.codebucket.mkkm.login;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.codebucket.mkkm.MobileKKM;

public class LoginHelper {

    private static final String PROFILE_URL = "https://m.kkm.krakow.pl/profile/%s";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private Context mContext;
    private String mFingerprint;

    public LoginHelper(Context context) {
        mContext = context;
        mFingerprint = MobileKKM.getPreferences().getString("fingerprint", null);
    }

    public String getToken(String login, String password) throws LoginFailedException {
        // Check if device has a network connection to the Internet
        if (!isNetworkConnectivity()) {
            throw new LoginFailedException(LoginError.NETWORK_NOT_AVAILABLE);
        }

        // Check if fingerprint exists, if not abort
        if (mFingerprint == null) {
            throw new LoginFailedException(LoginError.INVALID_FINGERPRINT);
        }

        OkHttpClient httpClient = new OkHttpClient();
        String token = null;

        try {
            // Add values to JSON
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("login", login);
            jsonBody.put("password", password);

            // Create POST request
            RequestBody body = RequestBody.create(JSON, jsonBody.toString());
            Request request = new Request.Builder()
                    .url(getEndpointUrl("login"))
                    .post(body)
                    .build();

            Response response = httpClient.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());

            // Check if response has error code (no 200 OK)
            if (!response.isSuccessful()) {
                if (jsonObject.has("message") && jsonObject.getString("message").equalsIgnoreCase("Problem logowania")) {
                    throw new LoginFailedException(LoginError.WRONG_CREDENTIALS);
                } else {
                    throw new LoginFailedException(LoginError.UNKNOWN_ERROR);
                }
            }

            token = jsonObject.getString("token");
        } catch (IOException ex) {
            // An IOException occurs only when the low-level connection failed
            throw new LoginFailedException(LoginError.NETWORK_NOT_AVAILABLE);
        } catch (JSONException ex) {
            // This shouldn't happen at all, unless something went wrong on the server
            throw new LoginFailedException(LoginError.UNKNOWN_ERROR);
        }

        return token;
    }

    public SessionProfile getSession(String token) {
        // Check if device has a network connection to the Internet or fingerprint is valid
        if (!isNetworkConnectivity() || mFingerprint == null) {
            return null;
        }

        OkHttpClient httpClient = new OkHttpClient();
        SessionProfile profile = null;

        try {
            // Create GET request
            Request request = new Request.Builder()
                    .url(getEndpointUrl("account"))
                    .addHeader("X-JWT-Assertion", token)
                    .addHeader("Content-Type", "application/json; charset=UTF-8")
                    .get()
                    .build();

            // Execute and load if successful
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                JSONObject account = new JSONObject(response.body().string());
                profile = new SessionProfile(mFingerprint, token).load(account);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

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
}