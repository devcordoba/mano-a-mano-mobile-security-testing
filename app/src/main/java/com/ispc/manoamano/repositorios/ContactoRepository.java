package com.ispc.manoamano.repositorios;

import android.content.Context;

import com.ispc.manoamano.api.ContactoApi;
import com.ispc.manoamano.api.RetrofitClient;
import com.ispc.manoamano.modelos.ContactoRequest;
import com.ispc.manoamano.modelos.ContactoResponse;

import retrofit2.Call;
import retrofit2.Callback;

public class ContactoRepository {

    private final ContactoApi contactoApi;

    public ContactoRepository(Context context) {
        contactoApi = RetrofitClient.getClient(context).create(ContactoApi.class);
    }

    public void crear(ContactoRequest request, Callback<ContactoResponse> callback) {
        Call<ContactoResponse> call = contactoApi.crear(request);
        call.enqueue(callback);
    }
}
