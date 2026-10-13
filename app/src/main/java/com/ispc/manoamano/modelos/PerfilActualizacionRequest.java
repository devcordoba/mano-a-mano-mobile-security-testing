package com.ispc.manoamano.modelos;

import com.google.gson.annotations.SerializedName;

public class PerfilActualizacionRequest {

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    private String email;
    private String telefono;

    @SerializedName("intereses_causas")
    private String interesesCausas;

    @SerializedName("disponibilidad_resumen")
    private String disponibilidadResumen;

    public PerfilActualizacionRequest(
            String firstName,
            String lastName,
            String email,
            String telefono,
            String interesesCausas,
            String disponibilidadResumen
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.telefono = telefono;
        this.interesesCausas = interesesCausas;
        this.disponibilidadResumen = disponibilidadResumen;
    }
}
