package com.ispc.manoamano.api;

import com.ispc.manoamano.modelos.Oportunidad;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OportunidadApi {

    @GET("api/oportunidades/")
    Call<List<Oportunidad>> obtenerOportunidades(
            @Query("activa") Boolean activa,
            @Query("q") String busqueda
    );

    @GET("api/oportunidades/{id}/")
    Call<Oportunidad> obtenerOportunidad(
            @Path("id") int id
    );
}
