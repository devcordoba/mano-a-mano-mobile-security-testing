package com.ispc.manoamano.modelos;

public class MensajeRequest {

    private int destinatario;

    private int oportunidad;

    private String cuerpo;

    public MensajeRequest(
            int destinatario,
            int oportunidad,
            String cuerpo
    ) {
        this.destinatario = destinatario;
        this.oportunidad = oportunidad;
        this.cuerpo = cuerpo;
    }

    public int getDestinatario() {
        return destinatario;
    }

    public int getOportunidad() {
        return oportunidad;
    }

    public String getCuerpo() {
        return cuerpo;
    }
}