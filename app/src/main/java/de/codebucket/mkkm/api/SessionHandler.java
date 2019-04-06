package de.codebucket.mkkm.api;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.List;

import de.codebucket.mkkm.api.adapter.DateLongFormatTypeAdapter;
import de.codebucket.mkkm.api.model.AuthToken;
import de.codebucket.mkkm.api.model.LoginRequest;
import de.codebucket.mkkm.api.service.AuthClient;
import de.codebucket.mkkm.api.service.KKMRestClient;
import de.codebucket.mkkm.api.util.AuthInterceptor;
import de.codebucket.mkkm.api.util.TokenAuthenticator;
import de.codebucket.mkkm.login.AccountUtils;
import de.codebucket.mkkm.model.Ticket;
import de.codebucket.mkkm.model.UserAccount;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SessionHandler {

    private static final String BASE_URL = "https://m.kkm.krakow.pl/";

    // these values aren't stored here anymore:
    // private String fingerprint; => stored in SharedPrefs
    // private String authToken; => stored as Account authToken
    private SharedPreferences sharedPrefs;
    private AuthClient authClient;
    private KKMRestClient kkmClient;

    public SessionHandler(Context context) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Retrofit with Gson adapters
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .registerTypeAdapter(Date.class, new DateLongFormatTypeAdapter())
                        .create()));

        // this is our basic retrofit instance for authentication
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit retrofit = builder.client(httpClient.build()).build();
        authClient = retrofit.create(AuthClient.class);

        // this instance is for all endpoints with auth required
        OkHttpClient.Builder authHttpClient = new OkHttpClient.Builder()
                .authenticator(new TokenAuthenticator(this))
                .addInterceptor(new AuthInterceptor(this));

        Retrofit authRetrofit = builder.client(authHttpClient.build()).build();
        kkmClient = authRetrofit.create(KKMRestClient.class);
    }

    public Call<AuthToken> login() {
        Account account = AccountUtils.getAccount();
        return login(account.name, AccountUtils.getPassword(account));
    }

    public Call<AuthToken> login(String email, String password) {
        return authClient.authenticate(getFingerprint(), new LoginRequest(email, password));
    }

    public Call<ResponseBody> recoverPassword(String email) {
        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        return authClient.recoverPassword(getFingerprint(), body);
    }

    public Call<UserAccount> getUserAccount() {
        return kkmClient.getUserAccount(getFingerprint());
    }

    public Call<List<Ticket>> getTickets() {
        return kkmClient.getTickets(getFingerprint());
    }

    public Call<ResponseBody> getPhoto(String photoId) {
        return kkmClient.getPhoto(photoId);
    }

    private String getFingerprint() {
        return sharedPrefs.getString("fingerprint", null);
    }

    public String getAuthToken() {
        return AccountUtils.getAuthToken(AccountUtils.getAccount());
    }

    public void setAuthToken(String authToken) {
        AccountUtils.setAuthToken(AccountUtils.getAccount(), authToken);
    }
}
