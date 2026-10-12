package com.ispc.manoamano.repositorios;

import android.content.Context;

import com.ispc.manoamano.api.RetrofitClient;
import com.ispc.manoamano.api.UsuarioApi;
import com.ispc.manoamano.modelos.RegistroRequest;
import com.ispc.manoamano.modelos.Usuario;

import retrofit2.Call;
import retrofit2.Callback;

public class UsuarioRepository {

    private final UsuarioApi usuarioApi;

    public UsuarioRepository(Context context) {

        usuarioApi = RetrofitClient
                .getClient(context)
                .create(UsuarioApi.class);
    }

    public void registrar(
            RegistroRequest request,
            Callback<Usuario> callback
    ) {
        Call<Usuario> call =
                usuarioApi.registrar(request);

        call.enqueue(callback);
    }
}
