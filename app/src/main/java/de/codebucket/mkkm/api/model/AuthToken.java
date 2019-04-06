package de.codebucket.mkkm.api.model;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthToken {

    private String token;

    public AuthToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public boolean isExpired() {
        if (token == null) {
            return true;
        }

        try {
            String[] parts = token.split("\\."); // Splitting header, payload and signature
            String decode = new String(Base64.decode(parts[1], Base64.URL_SAFE)); // Payload

            // Check if payload contains expiration timestamp
            JSONObject payload = new JSONObject(decode);
            if (!payload.has("exp")) {
                return true;
            }

            // Return true if token expiration timestamp is lower than current timestamp
            return payload.getLong("exp") < System.currentTimeMillis() / 1000;
        } catch (JSONException ignored) {}

        // Otherwise we assume it is expired
        return true;
    }
}
