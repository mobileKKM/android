package de.codebucket.mkkm.api;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.List;

import de.codebucket.mkkm.api.adapter.DateLongFormatTypeAdapter;
import de.codebucket.mkkm.api.adapter.TicketKindTypeAdapter;
import de.codebucket.mkkm.api.adapter.TicketStatusTypeAdapter;
import de.codebucket.mkkm.api.adapter.TicketTypeTypeAdapter;
import de.codebucket.mkkm.api.adapter.UserStatusTypeAdapter;
import de.codebucket.mkkm.api.model.AuthToken;
import de.codebucket.mkkm.api.model.LoginRequest;
import de.codebucket.mkkm.api.model.PasswordRequest;
import de.codebucket.mkkm.api.service.AuthClient;
import de.codebucket.mkkm.api.service.KKMRestClient;
import de.codebucket.mkkm.api.util.AuthInterceptor;
import de.codebucket.mkkm.api.util.LoggingInterceptor;
import de.codebucket.mkkm.api.util.TokenAuthenticator;
import de.codebucket.mkkm.sync.AccountUtils;
import de.codebucket.mkkm.model.Ticket;
import de.codebucket.mkkm.model.UserAccount;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SessionHandler {

    private static final String BASE_URL = "https://m.kkm.krakow.pl/";

    // Gson instance with type adapters registered
    private static final Gson sGson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateLongFormatTypeAdapter())
            .registerTypeAdapter(Ticket.TicketKind.class, new TicketKindTypeAdapter())
            .registerTypeAdapter(Ticket.TicketStatus.class, new TicketStatusTypeAdapter())
            .registerTypeAdapter(Ticket.TicketType.class, new TicketTypeTypeAdapter())
            .registerTypeAdapter(UserAccount.UserStatus.class, new UserStatusTypeAdapter())
            .create();

    // these values aren't stored here anymore:
    // private String fingerprint; => stored in SharedPrefs
    // private String authToken; => stored as Account authToken
    private SharedPreferences sharedPrefs;
    private AuthClient authClient;
    private KKMRestClient kkmClient;

    public SessionHandler(Context context) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Retrofit builder
        Retrofit.Builder builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(sGson))
                .baseUrl(BASE_URL);

        // Simple client for authentication only
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .build();

        // Authenticated client for other endpoints
        OkHttpClient authHttpClient = new OkHttpClient.Builder()
                .authenticator(new TokenAuthenticator(this))
                .addInterceptor(new AuthInterceptor(this))
                .build();

        Retrofit retrofit = builder.client(httpClient).build();
        authClient = retrofit.create(AuthClient.class);

        Retrofit authRetrofit = builder.client(authHttpClient).build();
        kkmClient = authRetrofit.create(KKMRestClient.class);
    }

    // TODO: move to AuthHandler
    public Call<AuthToken> login() {
        Account account = AccountUtils.getAccount();
        return login(account.name, AccountUtils.getPassword(account));
    }

    // TODO: move to AuthHandler
    public Call<AuthToken> login(String email, String password) {
        return authClient.authenticate(getFingerprint(), new LoginRequest(email, password));
    }

    // TODO: move to AuthHandler
    public Call<ResponseBody> recoverPassword(String email) {
        return authClient.recoverPassword(getFingerprint(), new PasswordRequest(email));
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
