package de.codebucket.mkkm.login;

import android.content.Context;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.R;

import static de.codebucket.mkkm.login.LoginFailedException.ErrorType;

public class LoginHelper {

    private static final String PROFILE_URL = "https://m.kkm.krakow.pl/profile/%s/%s";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private Context mContext;
    private String mFingerprint;

    public LoginHelper(Context context) {
        mContext = context;
        mFingerprint = MobileKKM.getPreferences().getString("fingerprint", null);
    }

    public String getToken(String login, String password) throws LoginFailedException {
        // Check if fingerprint exists, if not abort
        if (mFingerprint == null) {
            throw new LoginFailedException(ErrorType.USER, R.string.error_fingerprint);
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
                String errorMessage = mContext.getString(R.string.error_unknown);
                if (jsonObject.has("description")) {
                    errorMessage = jsonObject.getString("description");
                }

                throw new LoginFailedException(ErrorType.BACKEND, errorMessage);
            }

            token = jsonObject.getString("token");
        } catch (IOException ex) {
            // An IOException occurs only when the low-level connection failed
            throw new LoginFailedException(ErrorType.SYSTEM, R.string.error_no_network);
        } catch (JSONException ex) {
            // This shouldn't happen at all, unless something went wrong on the server
            throw new LoginFailedException(ErrorType.SYSTEM, R.string.error_unknown);
        }

        return token;
    }

    public SessionProfile getSession(String token) {
        // Check if fingerprint is valid
        if (mFingerprint == null) {
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

    public boolean isFingerprintValid() {
        return mFingerprint != null && mFingerprint.length() == 32;
    }

    private String getEndpointUrl(String endpoint) {
        return String.format(PROFILE_URL, mFingerprint, endpoint);
    }
}