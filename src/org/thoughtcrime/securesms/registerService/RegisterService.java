package org.thoughtcrime.securesms.registerService;

import org.thoughtcrime.securesms.registerService.entity.ContactFromApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RegisterService {

    @GET("users")
    Call<List<ContactFromApi>> loadAllContacts();
}
