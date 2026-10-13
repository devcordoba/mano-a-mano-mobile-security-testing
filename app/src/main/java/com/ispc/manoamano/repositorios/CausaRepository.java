package com.ispc.manoamano.repositorios;

import android.content.Context;

import com.ispc.manoamano.api.CausaApi;
import com.ispc.manoamano.api.RetrofitClient;
import com.ispc.manoamano.modelos.Causa;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class CausaRepository {

    private final CausaApi causaApi;

    public CausaRepository(Context context) {

        causaApi = RetrofitClient
                .getClient(context)
                .create(CausaApi.class);
    }

    public void obtenerCausas(
            Callback<List<Causa>> callback
    ) {
        Call<List<Causa>> call =
                causaApi.obtenerCausas();

        call.enqueue(callback);
    }
}
