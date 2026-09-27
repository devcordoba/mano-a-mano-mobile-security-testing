package com.ispc.manoamano.modelos;

import com.google.gson.annotations.SerializedName;

public class Postulacion {

    private int id;

    private int voluntario;

    @SerializedName("voluntario_username")
    private String voluntarioUsername;

    private int oportunidad;

    @SerializedName("oportunidad_titulo")
    private String oportunidadTitulo;

    @SerializedName("organizacion_propietario")
    private int organizacionPropietario;

    private String estado;

    @SerializedName("created_at")
    private String createdAt;

    public int getId() {
        return id;
    }

    public int getVoluntario() {
        return voluntario;
    }

    public String getVoluntarioUsername() {
        return voluntarioUsername;
    }

    public int getOportunidad() {
        return oportunidad;
    }

    public String getOportunidadTitulo() {
        return oportunidadTitulo;
    }

    public int getOrganizacionPropietario() {
        return organizacionPropietario;
    }

    public String getEstado() {
        return estado;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}