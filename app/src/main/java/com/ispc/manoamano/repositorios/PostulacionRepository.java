package com.ispc.manoamano.repositorios;

import android.content.Context;

import com.ispc.manoamano.api.PostulacionApi;
import com.ispc.manoamano.api.RetrofitClient;
import com.ispc.manoamano.modelos.Postulacion;
import com.ispc.manoamano.modelos.PostulacionBloque;
import com.ispc.manoamano.modelos.PostulacionRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class PostulacionRepository {

    private final PostulacionApi postulacionApi;

    public PostulacionRepository(Context context) {
        postulacionApi =
                RetrofitClient
                        .getClient(context)
                        .create(PostulacionApi.class);
    }

    public void obtenerPorVoluntario(
            int voluntarioId,
            Callback<List<Postulacion>> callback
    ) {
        Call<List<Postulacion>> call =
                postulacionApi.obtenerPorVoluntario(
                        voluntarioId
                );

        call.enqueue(callback);
    }

    public void crear(
            PostulacionRequest request,
            Callback<Postulacion> callback
    ) {
        Call<Postulacion> call =
                postulacionApi.crear(request);

        call.enqueue(callback);
    }

    public void editarEstado(
            int id,
            PostulacionRequest request,
            Callback<Postulacion> callback
    ) {
        Call<Postulacion> call =
                postulacionApi.editarEstado(
                        id,
                        request
                );

        call.enqueue(callback);
    }

    public void obtenerBloquesOrganizador(
            Callback<List<PostulacionBloque>> callback
    ) {
        Call<List<PostulacionBloque>> call =
                postulacionApi.obtenerBloquesOrganizador();

        call.enqueue(callback);
    }
}