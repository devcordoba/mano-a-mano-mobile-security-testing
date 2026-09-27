package com.ispc.manoamano.modelos;

import com.google.gson.annotations.SerializedName;

public class OportunidadRequest {

    private final int organizacion;
    private final String titulo;
    private final String descripcion;
    private final String ubicacion;
    private final int causa;

    @SerializedName("tipo_actividad")
    private final int tipoActividad;

    private final String disponibilidad;
    private final String requisitos;
    private final int cupos;

    @SerializedName("fecha_actividad")
    private final String fechaActividad;

    private final boolean activa;

    public OportunidadRequest(
            int organizacion,
            String titulo,
            String descripcion,
            String ubicacion,
            int causa,
            int tipoActividad,
            String disponibilidad,
            String requisitos,
            int cupos,
            String fechaActividad,
            boolean activa
    ) {
        this.organizacion = organizacion;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.causa = causa;
        this.tipoActividad = tipoActividad;
        this.disponibilidad = disponibilidad;
        this.requisitos = requisitos;
        this.cupos = cupos;
        this.fechaActividad = fechaActividad;
        this.activa = activa;
    }

    public int getOrganizacion() { return organizacion; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getUbicacion() { return ubicacion; }
    public int getCausa() { return causa; }
    public int getTipoActividad() { return tipoActividad; }
    public String getDisponibilidad() { return disponibilidad; }
    public String getRequisitos() { return requisitos; }
    public int getCupos() { return cupos; }
    public String getFechaActividad() { return fechaActividad; }
    public boolean isActiva() { return activa; }
}
