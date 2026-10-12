package com.ispc.manoamano.modelos;

public class PostulacionRequest {

    private int oportunidad;
    private String estado;

    public PostulacionRequest(int oportunidad) {
        this.oportunidad = oportunidad;
    }

    public PostulacionRequest(String estado) {
        this.estado = estado;
    }

    public int getOportunidad() {
        return oportunidad;
    }

    public String getEstado() {
        return estado;
    }
}