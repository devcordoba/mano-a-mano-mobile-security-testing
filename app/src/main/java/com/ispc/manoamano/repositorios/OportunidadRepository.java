package com.ispc.manoamano.repositorios;

import android.content.Context;

import com.ispc.manoamano.api.OportunidadApi;
import com.ispc.manoamano.api.RetrofitClient;
import com.ispc.manoamano.modelos.Oportunidad;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class OportunidadRepository {

    private final OportunidadApi oportunidadApi;

    public OportunidadRepository(Context context) {

        oportunidadApi = RetrofitClient
                .getClient(context)
                .create(OportunidadApi.class);
    }

    // Devuelve la llamada para poder cancelarla si llega una busqueda nueva.
    public Call<List<Oportunidad>> obtenerOportunidades(
            Boolean activa,
            String busqueda,
            Callback<List<Oportunidad>> callback
    ) {
        Call<List<Oportunidad>> call =
                oportunidadApi.obtenerOportunidades(
                        activa,
                        textoOpcional(busqueda)
                );

        call.enqueue(callback);
        return call;
    }

    public Call<Oportunidad> obtenerOportunidad(
            int id,
            Callback<Oportunidad> callback
    ) {
        Call<Oportunidad> call =
                oportunidadApi.obtenerOportunidad(id);

        call.enqueue(callback);
        return call;
    }

    // Retrofit omite los @Query nulos, asi una busqueda vacia trae todo el catalogo.
    private String textoOpcional(String texto) {
        if (texto == null) {
            return null;
        }
        String limpio = texto.trim();
        return limpio.isEmpty() ? null : limpio;
    }
}
