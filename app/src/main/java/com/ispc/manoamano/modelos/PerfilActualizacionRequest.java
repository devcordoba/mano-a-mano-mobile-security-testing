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

    private String password;

    @SerializedName("password_actual")
    private String passwordActual;

    public PerfilActualizacionRequest(
            String firstName,
            String lastName,
            String email,
            String telefono,
            String interesesCausas,
            String disponibilidadResumen
    ) {
        this(firstName, lastName, email, telefono, interesesCausas, disponibilidadResumen, null, null);
    }

    public PerfilActualizacionRequest(
            String firstName,
            String lastName,
            String email,
            String telefono,
            String interesesCausas,
            String disponibilidadResumen,
            String password,
            String passwordActual
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.telefono = telefono;
        this.interesesCausas = interesesCausas;
        this.disponibilidadResumen = disponibilidadResumen;
        this.password = password;
        this.passwordActual = passwordActual;
    }
}
