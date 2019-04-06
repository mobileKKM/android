package de.codebucket.mkkm.api.util;

import java.io.IOException;

import de.codebucket.mkkm.api.SessionHandler;

import okhttp3.Interceptor;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private SessionHandler sessionHandler;

    public AuthInterceptor(SessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String authToken = sessionHandler.getAuthToken();

        // check if Account authToken is not null
        if (authToken != null) {
            return chain.proceed(chain.request().newBuilder()
                    .addHeader("X-JWT-Assertion", authToken)
                    .build());
        } else {
            return chain.proceed(chain.request());
        }
    }
}
