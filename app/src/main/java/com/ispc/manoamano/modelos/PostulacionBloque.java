package com.ispc.manoamano.modelos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostulacionBloque {

    private String titulo;

    @SerializedName("oportunidad_id")
    private int oportunidadId;

    private List<Postulacion> items;

    public String getTitulo() {
        return titulo;
    }

    public int getOportunidadId() {
        return oportunidadId;
    }

    public List<Postulacion> getItems() {
        return items;
    }
}
