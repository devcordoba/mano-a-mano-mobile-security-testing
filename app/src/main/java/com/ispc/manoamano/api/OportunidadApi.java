package com.ispc.manoamano.api;

import com.ispc.manoamano.modelos.Oportunidad;
import com.ispc.manoamano.modelos.OportunidadGestion;
import com.ispc.manoamano.modelos.OportunidadRequest;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OportunidadApi {

    @GET("api/oportunidades/")
    Call<List<Oportunidad>> obtenerOportunidades(
            @Query("activa") Boolean activa,
            @Query("q") String busqueda
    );

    @GET("api/oportunidades/")
    Call<List<OportunidadGestion>> obtenerPorPropietario(
            @Query("propietario") int propietarioId,
            @Query("q") String busqueda
    );

    @GET("api/oportunidades/{id}/")
    Call<Oportunidad> obtenerOportunidad(
            @Path("id") int id
    );

    @POST("api/oportunidades/")
    Call<OportunidadGestion> crear(@Body OportunidadRequest request);

    @Multipart
    @POST("api/oportunidades/")
    Call<OportunidadGestion> crearConImagen(
            @PartMap Map<String, RequestBody> campos,
            @Part MultipartBody.Part imagen
    );

    @PATCH("api/oportunidades/{id}/")
    Call<OportunidadGestion> editar(
            @Path("id") int id,
            @Body OportunidadRequest request
    );

    @Multipart
    @PATCH("api/oportunidades/{id}/")
    Call<OportunidadGestion> editarConImagen(
            @Path("id") int id,
            @PartMap Map<String, RequestBody> campos,
            @Part MultipartBody.Part imagen
    );

    @DELETE("api/oportunidades/{id}/")
    Call<Void> eliminar(@Path("id") int id);

    @GET("api/oportunidades/{id}/imagen/")
    Call<ResponseBody> obtenerImagen(@Path("id") int id);
}
