package com.ispc.manoamano.modelos;

import com.google.gson.annotations.SerializedName;

public class Perfil {

    private String rol;

    private String telefono;

    @SerializedName("intereses_causas")
    private String interesesCausas;

    @SerializedName("disponibilidad_resumen")
    private String disponibilidadResumen;

    public String getRol() {
        return rol;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getInteresesCausas() {
        return interesesCausas;
    }

    public String getDisponibilidadResumen() {
        return disponibilidadResumen;
    }
}