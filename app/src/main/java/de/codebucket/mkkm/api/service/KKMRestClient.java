package de.codebucket.mkkm.api.service;

import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import de.codebucket.mkkm.model.Ticket;
import de.codebucket.mkkm.model.UserAccount;

public interface KKMRestClient {

    @GET("profile/{fingerprint}/account")
    Call<UserAccount> getUserAccount(@Path("fingerprint") String fingerprint);

    @GET("profile/{fingerprint}/tickets")
    Call<List<Ticket>> getTickets(@Path("fingerprint") String fingerprint);

    @GET("profile/{fingerprint}/ticket/{ticketId}/is-assigned")
    JsonObject isTicketAssigned(@Path("fingerprint") String fingerprint, @Path("ticketId") String ticketId);

    @GET("profile/{fingerprint}/ticket/{ticketId}/contract")
    ResponseBody getTicketContract(@Path("fingerprint") String fingerprint, @Path("ticketId") String ticketId);

    @GET("photo/{photoId}")
    Call<ResponseBody> getPhoto(@Path("photoId") String photoId);

}
