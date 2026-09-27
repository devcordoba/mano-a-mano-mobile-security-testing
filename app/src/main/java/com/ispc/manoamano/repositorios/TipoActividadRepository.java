package com.ispc.manoamano.repositorios;

import android.content.Context;

import com.ispc.manoamano.api.RetrofitClient;
import com.ispc.manoamano.api.TipoActividadApi;
import com.ispc.manoamano.modelos.TipoActividad;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class TipoActividadRepository {

    private final TipoActividadApi tipoActividadApi;

    public TipoActividadRepository(Context context) {

        tipoActividadApi = RetrofitClient
                .getClient(context)
                .create(TipoActividadApi.class);
    }

    public void obtenerTiposActividad(
            Callback<List<TipoActividad>> callback
    ) {
        Call<List<TipoActividad>> call =
                tipoActividadApi.obtenerTiposActividad();

        call.enqueue(callback);
    }
}
