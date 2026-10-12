package com.ispc.manoamano.modelos;

import com.google.gson.annotations.SerializedName;

// Mapea la respuesta de api/oportunidades/ (lista) y api/oportunidades/{id}/ (detalle).
public class Oportunidad {

    private int id;

    private String titulo;

    private String descripcion;

    private String requisitos;

    private String ubicacion;

    private Organizacion organizacion;

    private Causa causa;

    @SerializedName("tipo_actividad")
    private TipoActividad tipoActividad;

    private String disponibilidad;

    private int cupos;

    @SerializedName("fecha_actividad")
    private String fechaActividad;

    private boolean activa;

    @SerializedName("imagen_url")
    private String imagenUrl;

    private String resumen;

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getRequisitos() { return requisitos; }
    public String getUbicacion() { return ubicacion; }
    public Organizacion getOrganizacion() { return organizacion; }
    public Causa getCausa() { return causa; }
    public TipoActividad getTipoActividad() { return tipoActividad; }
    public String getDisponibilidad() { return disponibilidad; }
    public int getCupos() { return cupos; }
    public String getFechaActividad() { return fechaActividad; }
    public boolean isActiva() { return activa; }
    public String getImagenUrl() { return imagenUrl; }

    // La lista trae "resumen" y el detalle trae "descripcion".
    public String getResumen() { return resumen != null ? resumen : descripcion; }

    public String getNombreOrganizacion() {
        return organizacion == null || organizacion.getNombre() == null ? "" : organizacion.getNombre();
    }

    public String getNombreCausa() {
        return causa == null || causa.getNombre() == null ? "" : causa.getNombre();
    }

    public String getNombreTipoActividad() {
        return tipoActividad == null || tipoActividad.getNombre() == null ? "" : tipoActividad.getNombre();
    }
}
