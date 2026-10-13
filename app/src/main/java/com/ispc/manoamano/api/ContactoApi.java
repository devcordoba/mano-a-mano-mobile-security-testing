package com.ispc.manoamano.api;

import com.ispc.manoamano.modelos.ContactoRequest;
import com.ispc.manoamano.modelos.ContactoResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ContactoApi {

    @POST("api/contactos/")
    Call<ContactoResponse> crear(@Body ContactoRequest request);
}
