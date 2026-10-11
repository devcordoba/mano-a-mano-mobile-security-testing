package com.ispc.manoamano.modelos;

import com.google.gson.annotations.SerializedName;

public class Organizacion {

    private int id;

    private String nombre;

    @SerializedName("nombre_publico")
    private String nombrePublico;

    private String descripcion;

    @SerializedName("email_contacto")
    private String emailContacto;

    private String telefono;

    @SerializedName("sitio_web")
    private String sitioWeb;

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre != null ? nombre : nombrePublico;
    }

    public String getNombrePublico() { return nombrePublico; }

    public String getDescripcion() { return descripcion; }

    public String getEmailContacto() { return emailContacto; }

    public String getTelefono() { return telefono; }

    public String getSitioWeb() { return sitioWeb; }
}
