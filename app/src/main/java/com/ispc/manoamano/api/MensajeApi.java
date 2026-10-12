package com.ispc.manoamano.api;

import com.ispc.manoamano.modelos.BandejaMensajes;
import com.ispc.manoamano.modelos.Mensaje;
import com.ispc.manoamano.modelos.MensajeRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MensajeApi {

    @POST("api/mensajes/")
    Call<Mensaje> enviar(
            @Body MensajeRequest request
    );

    @GET("api/mensajes/bandeja/")
    Call<BandejaMensajes> obtenerBandeja(
            @Query("usuario") int usuarioId
    );
}