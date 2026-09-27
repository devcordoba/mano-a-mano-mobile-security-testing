package com.ispc.manoamano.repositorios;

import android.content.Context;

import com.ispc.manoamano.api.AuthApi;
import com.ispc.manoamano.api.RetrofitClient;
import com.ispc.manoamano.modelos.LoginRequest;
import com.ispc.manoamano.modelos.LoginResponse;
import com.ispc.manoamano.modelos.LogoutResponse;
import com.ispc.manoamano.modelos.MeResponse;
import com.ispc.manoamano.modelos.PerfilResponse;

import retrofit2.Call;
import retrofit2.Callback;

public class AuthRepository {

    private final AuthApi authApi;

    public AuthRepository(Context context) {

        authApi = RetrofitClient
                .getClient(context)
                .create(AuthApi.class);
    }

    public void login(
            LoginRequest request,
            Callback<LoginResponse> callback
    ) {
        Call<LoginResponse> call =
                authApi.login(request);

        call.enqueue(callback);
    }

    public void me(
            Callback<MeResponse> callback
    ) {
        Call<MeResponse> call =
                authApi.me();

        call.enqueue(callback);
    }

    public void perfil(
            Callback<PerfilResponse> callback
    ) {
        Call<PerfilResponse> call =
                authApi.perfil();

        call.enqueue(callback);
    }

    public void logout(
            Callback<LogoutResponse> callback
    ) {
        Call<LogoutResponse> call =
                authApi.logout();

        call.enqueue(callback);
    }
}