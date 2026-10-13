package com.ispc.manoamano.repositorios;

import android.content.Context;

import com.ispc.manoamano.api.OportunidadApi;
import com.ispc.manoamano.api.RetrofitClient;
import com.ispc.manoamano.modelos.Oportunidad;
import com.ispc.manoamano.modelos.OportunidadGestion;
import com.ispc.manoamano.modelos.OportunidadRequest;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class OportunidadRepository {

    private final OportunidadApi oportunidadApi;

    public OportunidadRepository(Context context) {

        oportunidadApi = RetrofitClient
                .getClient(context)
                .create(OportunidadApi.class);
    }

    public Call<List<Oportunidad>> obtenerOportunidades(
            boolean activa,
            Callback<List<Oportunidad>> callback
    ) {
        return obtenerOportunidades(activa, null, callback);
    }

    public Call<List<Oportunidad>> obtenerOportunidades(
            Boolean activa,
            String busqueda,
            Callback<List<Oportunidad>> callback
    ) {
        Call<List<Oportunidad>> call =
                oportunidadApi.obtenerOportunidades(
                        activa,
                        textoOpcional(busqueda)
                );

        call.enqueue(callback);
        return call;
    }

    public Call<List<OportunidadGestion>> obtenerPorPropietario(
            int propietarioId,
            String busqueda,
            Callback<List<OportunidadGestion>> callback
    ) {
        Call<List<OportunidadGestion>> call = oportunidadApi.obtenerPorPropietario(
                propietarioId,
                textoOpcional(busqueda)
        );
        call.enqueue(callback);
        return call;
    }

    public Call<Oportunidad> obtenerOportunidad(
            int id,
            Callback<Oportunidad> callback
    ) {
        Call<Oportunidad> call =
                oportunidadApi.obtenerOportunidad(id);

        call.enqueue(callback);
        return call;
    }

    public Call<OportunidadGestion> crear(
            OportunidadRequest request,
            Callback<OportunidadGestion> callback
    ) {
        Call<OportunidadGestion> call = oportunidadApi.crear(request);
        call.enqueue(callback);
        return call;
    }

    public Call<OportunidadGestion> crearConImagen(
            OportunidadRequest request,
            File imagen,
            String contentType,
            Callback<OportunidadGestion> callback
    ) {
        Call<OportunidadGestion> call = oportunidadApi.crearConImagen(
                construirCuerpo(request, imagen, contentType)
        );
        call.enqueue(callback);
        return call;
    }

    public Call<OportunidadGestion> editar(
            int id,
            OportunidadRequest request,
            Callback<OportunidadGestion> callback
    ) {
        Call<OportunidadGestion> call = oportunidadApi.editar(id, request);
        call.enqueue(callback);
        return call;
    }

    public Call<OportunidadGestion> editarConImagen(
            int id,
            OportunidadRequest request,
            File imagen,
            String contentType,
            Callback<OportunidadGestion> callback
    ) {
        Call<OportunidadGestion> call = oportunidadApi.editarConImagen(
                id,
                construirCuerpo(request, imagen, contentType)
        );
        call.enqueue(callback);
        return call;
    }

    public Call<Void> eliminar(int id, Callback<Void> callback) {
        Call<Void> call = oportunidadApi.eliminar(id);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseBody> obtenerImagen(int id, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = oportunidadApi.obtenerImagen(id);
        call.enqueue(callback);
        return call;
    }

    private RequestBody construirCuerpo(OportunidadRequest request, File imagen, String contentType) {
        if (imagen == null || !imagen.isFile()) {
            throw new IllegalArgumentException("La imagen indicada no existe.");
        }
        String tipo = contentType == null || contentType.trim().isEmpty()
                ? "image/jpeg"
                : contentType;
        MultipartBody.Builder cuerpo = new MultipartBody.Builder().setType(MultipartBody.FORM);
        agregarCampo(cuerpo, "organizacion", request.getOrganizacion());
        agregarCampo(cuerpo, "titulo", request.getTitulo());
        agregarCampo(cuerpo, "descripcion", request.getDescripcion());
        agregarCampo(cuerpo, "ubicacion", request.getUbicacion());
        agregarCampo(cuerpo, "causa", request.getCausa());
        agregarCampo(cuerpo, "tipo_actividad", request.getTipoActividad());
        agregarCampo(cuerpo, "disponibilidad", request.getDisponibilidad());
        agregarCampo(cuerpo, "requisitos", request.getRequisitos());
        agregarCampo(cuerpo, "cupos", request.getCupos());
        agregarCampo(cuerpo, "fecha_actividad", request.getFechaActividad());
        agregarCampo(cuerpo, "activa", request.isActiva());
        cuerpo.addFormDataPart(
                "imagen",
                imagen.getName(),
                RequestBody.create(MediaType.parse(tipo), imagen)
        );
        return cuerpo.build();
    }

    private void agregarCampo(MultipartBody.Builder cuerpo, String nombre, Object valor) {
        if (valor == null) {
            return;
        }
        cuerpo.addFormDataPart(nombre, String.valueOf(valor));
    }

    private String textoOpcional(String texto) {
        if (texto == null) {
            return null;
        }
        String limpio = texto.trim();
        return limpio.isEmpty() ? null : limpio;
    }
}
