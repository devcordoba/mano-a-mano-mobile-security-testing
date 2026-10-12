package com.ispc.manoamano.api;

import com.ispc.manoamano.modelos.Causa;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CausaApi {

    @GET("api/causas/")
    Call<List<Causa>> obtenerCausas();
}