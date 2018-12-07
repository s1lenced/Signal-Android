package org.thoughtcrime.securesms.registerService;

import org.thoughtcrime.securesms.registerService.entity.ContactFromApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RegisterService {

    @GET("users")
    Call<List<ContactFromApi>> loadAllContacts();

    @POST("user")
    Call<ContactFromApi> addUser(@Body ContactFromApi contact);

    @PATCH("user/{number}")
    Call<ContactFromApi> updateUser(@Body ContactFromApi contact, @Path("number") String number);

    @PATCH("users/verify")
    Call<ContactFromApi> verifyUser(@Body ContactFromApi contact);

    @POST("join")
    Call<ContactFromApi> join(@Body ContactFromApi contact);

    @GET("user/{number}")
    Call<ContactFromApi> singleUser(@Path("number") String number);
}
