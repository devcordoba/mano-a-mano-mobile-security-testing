package com.ispc.manoamano.api;

import com.ispc.manoamano.modelos.LoginRequest;
import com.ispc.manoamano.modelos.LoginResponse;
import com.ispc.manoamano.modelos.LogoutResponse;
import com.ispc.manoamano.modelos.MeResponse;
import com.ispc.manoamano.modelos.PerfilResponse;

import retrofit2.Call;
import com.ispc.manoamano.modelos.PerfilActualizacionRequest;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("api/auth/login/")
    Call<LoginResponse> login(
            @Body LoginRequest request
    );

    @GET("api/auth/me/")
    Call<MeResponse> me();

    @GET("api/auth/perfil/")
    Call<PerfilResponse> perfil();

    @PATCH("api/auth/perfil/")
    Call<PerfilResponse> actualizarPerfil(
            @Body PerfilActualizacionRequest request
    );

    @DELETE("api/auth/perfil/")
    Call<ResponseBody> eliminarCuenta();

    @POST("api/auth/logout/")
    Call<LogoutResponse> logout();
}