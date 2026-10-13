package com.ispc.manoamano.api;

import com.ispc.manoamano.modelos.Usuario;
import com.ispc.manoamano.modelos.RegistroRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsuarioApi {

    @POST("api/usuarios/")
    Call<Usuario> registrar(
            @Body RegistroRequest request
    );
}