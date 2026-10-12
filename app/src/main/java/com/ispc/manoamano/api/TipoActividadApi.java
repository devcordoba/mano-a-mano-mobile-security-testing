package com.ispc.manoamano.api;

import com.ispc.manoamano.modelos.TipoActividad;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TipoActividadApi {

    @GET("api/tipos-actividad/")
    Call<List<TipoActividad>> obtenerTiposActividad();
}