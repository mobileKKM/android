package de.codebucket.mkkm.login;

import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

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

import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.R;
import de.codebucket.mkkm.database.model.Account;
import de.codebucket.mkkm.database.model.Photo;
import de.codebucket.mkkm.database.model.Ticket;
import de.codebucket.mkkm.util.adapter.DateLongFormatTypeAdapter;
import de.codebucket.mkkm.util.adapter.TicketStatusTypeAdapter;

public class LoginHelper {

    // mKKM endpoint URLs
    private static final String PROFILE_URL = "https://m.kkm.krakow.pl/profile/%s/%s";
    private static final String PHOTO_URL = "https://m.kkm.krakow.pl/photo/%s";

    // JSON stuff
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Gson sGson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateLongFormatTypeAdapter())
            .registerTypeAdapter(Ticket.TicketStatus.class, new TicketStatusTypeAdapter())
            .create();

    // Device related variables (those aren't null)
    private AccountManager mAccountManager;
    private String mFingerprint;

    // Login related variables
    private String mSessionToken;
    private OkHttpClient mHttpClient = new OkHttpClient();

    public LoginHelper(Context context) {
        mAccountManager = AccountManager.get(context);
        mFingerprint = MobileKKM.getPreferences().getString("fingerprint", null);
    }

    public void login() throws LoginFailedException {
        // Get account from device
        android.accounts.Account account = null;

        try {
            account = mAccountManager.getAccountsByType(AuthenticatorService.ACCOUNT_TYPE)[0];
        } catch (Exception ignored) {}

        // Don't continue if no account stored on device
        if (account == null) {
            throw new LoginFailedException(LoginFailedException.ErrorType.USER, R.string.error_account);
        }

        String password = mAccountManager.getPassword(account);
        login(account.name, password);
    }

    public void login(final String login, final String password) throws LoginFailedException {
        // Check if fingerprint exists, if not abort
        if (!isFingerprintValid()) {
            throw new LoginFailedException(LoginFailedException.ErrorType.USER, R.string.error_fingerprint);
        }

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

            Response response = mHttpClient.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());

            // Check if response has error code (no 200 OK)
            if (!response.isSuccessful()) {
                String errorMessage = getString(R.string.error_unknown);
                if (jsonObject.has("description")) {
                    errorMessage = jsonObject.getString("description");
                }

                throw new LoginFailedException(LoginFailedException.ErrorType.BACKEND, errorMessage);
            }

            mSessionToken = jsonObject.getString("token");
        } catch (IOException ex) {
            // An IOException occurs only when the low-level connection failed
            throw new LoginFailedException(LoginFailedException.ErrorType.SYSTEM, R.string.error_no_network);
        } catch (JSONException ex) {
            // This shouldn't happen at all, unless something went wrong on the server
            throw new LoginFailedException(LoginFailedException.ErrorType.UNKNOWN, R.string.error_unknown);
        }
    }

    public Account getAccount() {
        Account account = null;

        try {
            // Create GET request
            Request request = new Request.Builder()
                    .url(getEndpointUrl("account"))
                    .addHeader("X-JWT-Assertion", mSessionToken)
                    .addHeader("Content-Type", "application/json; charset=UTF-8")
                    .get()
                    .build();

            // Execute and load if successful
            Response response = mHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                account = sGson.fromJson(response.body().charStream(), Account.class);
            }

            // Refresh session token
            mSessionToken = response.header("X-JWT-Assertion", mSessionToken);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return account;
    }

    public List<Ticket> getTickets() {
        List<Ticket> tickets = new ArrayList<>();

        try {
            // Create GET request
            Request request = new Request.Builder()
                    .url(getEndpointUrl("tickets"))
                    .addHeader("X-JWT-Assertion", mSessionToken)
                    .addHeader("Content-Type", "application/json; charset=UTF-8")
                    .get()
                    .build();

            // Execute and load if successful
            Response response = mHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                Type listType = new TypeToken<List<Ticket>>(){}.getType();
                tickets = sGson.fromJson(response.body().charStream(), listType);
            }

            // Refresh session token
            mSessionToken = response.header("X-JWT-Assertion", mSessionToken);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return tickets;
    }

    public Photo getPhoto(Account account) {
        Photo photo = null;

        try {
            // Create GET request
            Request request = new Request.Builder()
                    .url(getPhotoUrl(account.getPhotoId()))
                    .addHeader("X-JWT-Assertion", mSessionToken)
                    .addHeader("Referer", "https://m.kkm.krakow.pl/")
                    .get()
                    .build();

            // Execute and load if successful
            Response response = mHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                photo = Photo.fromBitmap(account, bitmap);
            }

            // Refresh session token
            mSessionToken = response.header("X-JWT-Assertion", mSessionToken);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return photo;
    }

    public boolean isFingerprintValid() {
        return mFingerprint != null && mFingerprint.length() == 32;
    }

    public boolean isSessionExpired() {
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

    private String getString(int resId) {
        return MobileKKM.getInstance().getString(resId);
    }
}
