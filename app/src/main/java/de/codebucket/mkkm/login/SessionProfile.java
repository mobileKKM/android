package de.codebucket.mkkm.login;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class SessionProfile implements Serializable {

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
}
