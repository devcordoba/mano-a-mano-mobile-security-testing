package com.ispc.manoamano.modelos;

public class Oportunidad {
    private final int id;
    private final String titulo;
    private final String ubicacion;
    private final String organizacion;
    private final String causa;
    private final String tipoActividad;
    private final String disponibilidad;
    private final int cupos;
    private final String fechaActividad;
    private final boolean activa;
    private final int imagenResId;
    private final String resumen;

    public Oportunidad(int id, String titulo, String ubicacion, String organizacion,
                       String causa, String tipoActividad, String disponibilidad,
                       int cupos, String fechaActividad, boolean activa,
                       int imagenResId, String resumen) {
        this.id = id;
        this.titulo = titulo;
        this.ubicacion = ubicacion;
        this.organizacion = organizacion;
        this.causa = causa;
        this.tipoActividad = tipoActividad;
        this.disponibilidad = disponibilidad;
        this.cupos = cupos;
        this.fechaActividad = fechaActividad;
        this.activa = activa;
        this.imagenResId = imagenResId;
        this.resumen = resumen;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getUbicacion() { return ubicacion; }
    public String getOrganizacion() { return organizacion; }
    public String getCausa() { return causa; }
    public String getTipoActividad() { return tipoActividad; }
    public String getDisponibilidad() { return disponibilidad; }
    public int getCupos() { return cupos; }
    public String getFechaActividad() { return fechaActividad; }
    public boolean isActiva() { return activa; }
    public int getImagenResId() { return imagenResId; }
    public String getResumen() { return resumen; }
}
