package de.codebucket.mkkm.api.service;

import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.http.POST;

import de.codebucket.mkkm.api.model.AuthToken;
import de.codebucket.mkkm.api.model.LoginRequest;
import de.codebucket.mkkm.api.model.PasswordRequest;

public interface AuthClient {

    @POST("profile/{fingerprint}/login")
    Call<AuthToken> authenticate(@Path("fingerprint") String fingerprint, @Body LoginRequest login);

    @POST("profile/{fingerprint}/recover-password")
    Call<ResponseBody> recoverPassword(@Path("fingerprint") String fingerprint, @Body PasswordRequest password);

}
