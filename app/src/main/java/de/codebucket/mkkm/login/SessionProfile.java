package de.codebucket.mkkm.login;

import android.util.Base64;
import android.widget.ImageView;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

import de.codebucket.mkkm.R;

public class SessionProfile implements Serializable {

    private static final String PHOTO_URL = "https://m.kkm.krakow.pl/photo/%s";

    // session related
    private final String mFingerprint;
    private final String mToken;

    // account related
    private String passengerId;
    private String firstName;
    private String lastName;
    private String email;
    private String photoId;

    public SessionProfile(String fingerprint, String token) {
        mFingerprint = fingerprint;
        mToken = token;
    }

    public String getFingerprint() {
        return mFingerprint;
    }

    public String getToken() {
        return mToken;
    }

    public SessionProfile load(JSONObject account) throws JSONException {
        passengerId = account.getString("passenger_id");
        firstName = account.getString("first_name");
        lastName = account.getString("last_name");
        email = account.getString("email");
        photoId = account.getString("photo_id");
        return this;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return email;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void loadPhoto(ImageView view) {
        // we need to supply custom header with our session token
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("X-JWT-Assertion", mToken)
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        Picasso picasso = new Picasso.Builder(view.getContext())
                .downloader(new OkHttp3Downloader(client))
                .build();

        // load image into view
        String photoUrl = String.format(PHOTO_URL, photoId);
        picasso.load(photoUrl).error(R.drawable.kkm_avatar).into(view);
    }

    public boolean isTokenExpired() {
        try {
            String[] parts = mToken.split("\\."); // Splitting header, payload and signature
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
}
