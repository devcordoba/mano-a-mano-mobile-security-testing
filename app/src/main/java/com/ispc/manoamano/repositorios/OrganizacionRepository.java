package com.ispc.manoamano.repositorios;

import android.content.Context;

import com.ispc.manoamano.api.OrganizacionApi;
import com.ispc.manoamano.api.RetrofitClient;
import com.ispc.manoamano.modelos.Organizacion;
import com.ispc.manoamano.modelos.OrganizacionRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class OrganizacionRepository {

    private final OrganizacionApi organizacionApi;

    public OrganizacionRepository(Context context) {
        organizacionApi =
                RetrofitClient
                        .getClient(context)
                        .create(OrganizacionApi.class);
    }

    public void obtenerPorPropietario(
            int propietarioId,
            Callback<List<Organizacion>> callback
    ) {
        Call<List<Organizacion>> call =
                organizacionApi.obtenerPorPropietario(
                        propietarioId
                );

        call.enqueue(callback);
    }

    public void crear(
            OrganizacionRequest request,
            Callback<Organizacion> callback
    ) {
        Call<Organizacion> call =
                organizacionApi.crear(request);

        call.enqueue(callback);
    }

    public void editar(
            int id,
            OrganizacionRequest request,
            Callback<Organizacion> callback
    ) {
        Call<Organizacion> call =
                organizacionApi.editar(id, request);

        call.enqueue(callback);
    }

    public void eliminar(
            int id,
            Callback<Void> callback
    ) {
        Call<Void> call =
                organizacionApi.eliminar(id);

        call.enqueue(callback);
    }
}