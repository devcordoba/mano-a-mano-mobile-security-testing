package com.ispc.manoamano.api;

import com.ispc.manoamano.modelos.Postulacion;
import com.ispc.manoamano.modelos.PostulacionBloque;
import com.ispc.manoamano.modelos.PostulacionRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostulacionApi {

    @GET("api/postulaciones/")
    Call<List<Postulacion>> obtenerPorVoluntario(
            @Query("voluntario") int voluntarioId
    );

    @POST("api/postulaciones/")
    Call<Postulacion> crear(
            @Body PostulacionRequest request
    );

    @PATCH("api/postulaciones/{id}/")
    Call<Postulacion> editarEstado(
            @Path("id") int id,
            @Body PostulacionRequest request
    );

    @GET("api/postulaciones/bloques-organizador/")
    Call<List<PostulacionBloque>> obtenerBloquesOrganizador();
}
