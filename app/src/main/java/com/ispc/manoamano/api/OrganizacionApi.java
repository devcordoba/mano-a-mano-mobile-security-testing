package com.ispc.manoamano.api;

import com.ispc.manoamano.modelos.Organizacion;
import com.ispc.manoamano.modelos.OrganizacionRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Path;

public interface OrganizacionApi {

    @GET("api/organizaciones/")
    Call<List<Organizacion>> obtenerPorPropietario(
            @Query("propietario") int propietarioId
    );

    @POST("api/organizaciones/")
    Call<Organizacion> crear(
            @Body OrganizacionRequest request
    );

    @PATCH("api/organizaciones/{id}/")
    Call<Organizacion> editar(
            @Path("id") int id,
            @Body OrganizacionRequest request
    );

    @DELETE("api/organizaciones/{id}/")
    Call<Void> eliminar(
            @Path("id") int id
    );
}