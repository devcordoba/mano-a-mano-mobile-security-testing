package com.ispc.manoamano.repositorios;

import android.content.Context;

import com.ispc.manoamano.api.OportunidadApi;
import com.ispc.manoamano.api.RetrofitClient;
import com.ispc.manoamano.modelos.Oportunidad;
import com.ispc.manoamano.modelos.OportunidadGestion;
import com.ispc.manoamano.modelos.OportunidadRequest;

import java.io.File;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

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
                construirCampos(request),
                construirImagen(imagen, contentType)
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
                construirCampos(request),
                construirImagen(imagen, contentType)
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

    private Map<String, RequestBody> construirCampos(OportunidadRequest request) {
        Map<String, RequestBody> campos = new LinkedHashMap<>();
        agregarCampo(campos, "organizacion", request.getOrganizacion());
        agregarCampo(campos, "titulo", request.getTitulo());
        agregarCampo(campos, "descripcion", request.getDescripcion());
        agregarCampo(campos, "ubicacion", request.getUbicacion());
        agregarCampo(campos, "causa", request.getCausa());
        agregarCampo(campos, "tipo_actividad", request.getTipoActividad());
        agregarCampo(campos, "disponibilidad", request.getDisponibilidad());
        agregarCampo(campos, "requisitos", request.getRequisitos());
        agregarCampo(campos, "cupos", request.getCupos());
        agregarCampo(campos, "fecha_actividad", request.getFechaActividad());
        agregarCampo(campos, "activa", request.isActiva());
        return campos;
    }

    private void agregarCampo(Map<String, RequestBody> campos, String nombre, Object valor) {
        if (valor == null) {
            return;
        }
        campos.put(
                nombre,
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(valor))
        );
    }

    private MultipartBody.Part construirImagen(File imagen, String contentType) {
        if (imagen == null || !imagen.isFile()) {
            throw new IllegalArgumentException("La imagen indicada no existe.");
        }
        String tipo = contentType == null || contentType.trim().isEmpty()
                ? "application/octet-stream"
                : contentType;
        RequestBody body = RequestBody.create(MediaType.parse(tipo), imagen);
        return MultipartBody.Part.createFormData("imagen", imagen.getName(), body);
    }

    private String textoOpcional(String texto) {
        if (texto == null) {
            return null;
        }
        String limpio = texto.trim();
        return limpio.isEmpty() ? null : limpio;
    }
}
