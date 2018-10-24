package de.codebucket.mkkm.login;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import de.codebucket.mkkm.database.model.Account;
import de.codebucket.mkkm.database.model.Photo;
import de.codebucket.mkkm.database.model.Ticket;
import de.codebucket.mkkm.util.Const;
import de.codebucket.mkkm.util.EncryptUtils;
import de.codebucket.mkkm.util.adapter.DateLongFormatTypeAdapter;
import de.codebucket.mkkm.util.adapter.TicketStatusTypeAdapter;

public class LoginHelper {

    private static final String TAG = "LoginHelper";

    // mKKM endpoint URLs
    private static final String PROFILE_URL = "https://m.kkm.krakow.pl/profile/%s/%s";
    private static final String PHOTO_URL = "https://m.kkm.krakow.pl/photo/%s";

    // JSON stuff
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Gson sGson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateLongFormatTypeAdapter())
            .registerTypeAdapter(Ticket.TicketStatus.class, new TicketStatusTypeAdapter())
            .create();

    // Login related variables
    private String mFingerprint, mSessionToken;
    private OkHttpClient mHttpClient = new OkHttpClient();

    public LoginHelper(String fingerprint) {
        mFingerprint = fingerprint;
    }

    public int login() throws LoginFailedException {
        // Get account from device
        android.accounts.Account account = AccountUtils.getAccount();

        // Don't continue if no account stored on device
        if (account == null) {
            return Const.ErrorCode.NO_ACCOUNT;
        }

        // Check if stored password is encrypted
        String encryptedPassword = AccountUtils.getPasswordEncrypted(account);
        if (!EncryptUtils.isBase64(encryptedPassword)) {
            return Const.ErrorCode.INVALID_CREDENTIALS;
        }

        String password = AccountUtils.getPassword(account);
        return hasSessionExpired() ? login(account.name, password) : Const.ErrorCode.SUCCESS;
    }

    public int login(final String login, final String password) throws LoginFailedException {
        // Check if fingerprint exists, if not abort
        if (!isFingerprintValid()) {
            return Const.ErrorCode.INVALID_FINGERPRINT;
        }

        // We need to close that later
        Response response = null;

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

            response = mHttpClient.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());

            // Check if response has error code (no 200 OK)
            if (!response.isSuccessful()) {
                if (jsonObject.has("description")) {
                    String errorMessage = jsonObject.getString("description");
                    throw new LoginFailedException(errorMessage);
                }

                return Const.ErrorCode.SERVER_ERROR;
            }

            mSessionToken = jsonObject.getString("token");
        } catch (IOException ex) {
            // An IOException occurs only when the low-level connection failed
            return Const.ErrorCode.CONNECTION_ERROR;
        } catch (JSONException ex) {
            // This shouldn't happen at all, unless something went wrong on the server
            return Const.ErrorCode.WRONG_RESPONSE;
        } finally {
            if (response != null) {
                response.body().close();
            }
        }

        return Const.ErrorCode.SUCCESS;
    }

    private Response executeCall(String url) throws IOException {
        Log.d(TAG, "Executing GET: " + url);

        // Create GET request
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-JWT-Assertion", mSessionToken)
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .get()
                .build();

        // Execute and load if successful
        Response response = mHttpClient.newCall(request).execute();

        // Update session token
        mSessionToken = response.header("X-JWT-Assertion", mSessionToken);
        return response;
    }

    public Account getAccount() throws IOException {
        Response response = executeCall(getEndpointUrl("account"));
        if (response.isSuccessful()) {
            Type classType = new TypeToken<Account>(){}.getType();
            return sGson.fromJson(response.body().charStream(), classType);
        }

        return null;
    }

    public List<Ticket> getTickets() throws IOException {
        Response response = executeCall(getEndpointUrl("tickets"));
        if (response.isSuccessful()) {
            Type listType = new TypeToken<List<Ticket>>(){}.getType();
            return sGson.fromJson(response.body().charStream(), listType);
        }

        return new ArrayList<>();
    }

    public Photo getPhoto(Account account) throws IOException {
        Response response = executeCall(getPhotoUrl(account.getPhotoId()));
        if (response.isSuccessful()) {
            Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
            return Photo.fromBitmap(account, bitmap);
        }

        return null;
    }

    public void updateFingerprint(String fingerprint) {
        mFingerprint = fingerprint;
        mSessionToken = null;
    }

    public boolean hasSessionExpired() {
        if (mSessionToken == null) {
            return true;
        }

        try {
            String[] parts = mSessionToken.split("\\."); // Splitting header, payload and signature
            String decode = new String(Base64.decode(parts[1], Base64.URL_SAFE)); // Payload

            // Check if payload contains expiration timestamp
            JSONObject payload = new JSONObject(decode);
            if (!payload.has("exp")) {
                return true;
            }

            // Return true if token expiration timestamp is lower than current timestamp
            return payload.getLong("exp") < System.currentTimeMillis() / 1000;
        } catch (JSONException ignored) {}
        return true;
    }

    public boolean isFingerprintValid() {
        return mFingerprint != null && mFingerprint.length() == 32;
    }

    public String getFingerprint() {
        return mFingerprint;
    }

    public String getSessionToken() {
        return mSessionToken;
    }

    private String getEndpointUrl(String endpoint) {
        return String.format(PROFILE_URL, mFingerprint, endpoint);
    }

    private String getPhotoUrl(String photoId) {
        return String.format(PHOTO_URL, photoId);
    }
}
