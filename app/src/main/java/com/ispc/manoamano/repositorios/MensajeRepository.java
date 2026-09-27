package com.ispc.manoamano.repositorios;

import android.content.Context;

import com.ispc.manoamano.api.MensajeApi;
import com.ispc.manoamano.api.RetrofitClient;
import com.ispc.manoamano.modelos.BandejaMensajes;
import com.ispc.manoamano.modelos.Mensaje;
import com.ispc.manoamano.modelos.MensajeRequest;

import retrofit2.Call;
import retrofit2.Callback;

public class MensajeRepository {

    private final MensajeApi mensajeApi;

    public MensajeRepository(Context context) {

        mensajeApi = RetrofitClient
                .getClient(context)
                .create(MensajeApi.class);
    }

    public void enviar(
            MensajeRequest request,
            Callback<Mensaje> callback
    ) {
        Call<Mensaje> call =
                mensajeApi.enviar(request);

        call.enqueue(callback);
    }

    public void obtenerBandeja(
            int usuarioId,
            Callback<BandejaMensajes> callback
    ) {
        Call<BandejaMensajes> call =
                mensajeApi.obtenerBandeja(usuarioId);

        call.enqueue(callback);
    }
}
