package com.ispc.manoamano.modelos;

import com.google.gson.annotations.SerializedName;

public class OrganizacionRequest {

    @SerializedName("nombre_publico")
    private String nombrePublico;

    private String descripcion;

    @SerializedName("email_contacto")
    private String emailContacto;

    private String telefono;

    @SerializedName("sitio_web")
    private String sitioWeb;

    public OrganizacionRequest(
            String nombrePublico,
            String descripcion,
            String emailContacto,
            String telefono,
            String sitioWeb
    ) {
        this.nombrePublico = nombrePublico;
        this.descripcion = descripcion;
        this.emailContacto = emailContacto;
        this.telefono = telefono;
        this.sitioWeb = sitioWeb;
    }

    public String getNombrePublico() {
        return nombrePublico;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getEmailContacto() {
        return emailContacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }
}