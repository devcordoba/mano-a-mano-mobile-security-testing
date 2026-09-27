package com.ispc.manoamano.modelos;

import com.google.gson.annotations.SerializedName;

public class OportunidadGestion {

    private int id;
    private int organizacion;

    @SerializedName("organizacion_nombre")
    private String organizacionNombre;

    private String titulo;
    private String descripcion;
    private String ubicacion;
    private int causa;

    @SerializedName("tipo_actividad")
    private int tipoActividad;

    @SerializedName("tipo_actividad_nombre")
    private String tipoActividadNombre;

    private String disponibilidad;
    private String requisitos;
    private int cupos;

    @SerializedName("fecha_actividad")
    private String fechaActividad;

    private boolean activa;

    @SerializedName("tiene_imagen")
    private boolean tieneImagen;

    @SerializedName("updated_at")
    private String updatedAt;

    public int getId() { return id; }
    public int getOrganizacion() { return organizacion; }
    public String getOrganizacionNombre() { return organizacionNombre; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getUbicacion() { return ubicacion; }
    public int getCausa() { return causa; }
    public int getTipoActividad() { return tipoActividad; }
    public String getTipoActividadNombre() { return tipoActividadNombre; }
    public String getDisponibilidad() { return disponibilidad; }
    public String getRequisitos() { return requisitos; }
    public int getCupos() { return cupos; }
    public String getFechaActividad() { return fechaActividad; }
    public boolean isActiva() { return activa; }
    public boolean isTieneImagen() { return tieneImagen; }
    public String getUpdatedAt() { return updatedAt; }
}
