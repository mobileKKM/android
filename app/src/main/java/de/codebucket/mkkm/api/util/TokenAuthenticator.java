package de.codebucket.mkkm.api.util;

import java.io.IOException;

import de.codebucket.mkkm.api.SessionHandler;
import de.codebucket.mkkm.api.model.AuthToken;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {

    private SessionHandler sessionHandler;

    public TokenAuthenticator(SessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        // check if response code is 401 Not Authorized
        if (response.code() == 401) {
            retrofit2.Response<AuthToken> authResponse = sessionHandler.login().execute();
            if (authResponse.isSuccessful()) {
                String authToken = authResponse.body().getToken();

                // update Account authToken and proceed request
                sessionHandler.setAuthToken(authToken);
                return response.request().newBuilder()
                        .header("X-JWT-Assertion", authToken)
                        .build();
            }
        }

        return null;
    }
}
