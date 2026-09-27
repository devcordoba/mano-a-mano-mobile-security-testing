package com.ispc.manoamano.modelos;

import com.google.gson.annotations.SerializedName;

public class Mensaje {

    private int id;

    private int remitente;

    @SerializedName("remitente_username")
    private String remitenteUsername;

    private int destinatario;

    @SerializedName("destinatario_username")
    private String destinatarioUsername;

    private int oportunidad;

    private String cuerpo;

    @SerializedName("created_at")
    private String createdAt;

    public int getId() {
        return id;
    }

    public int getRemitente() {
        return remitente;
    }

    public String getRemitenteUsername() {
        return remitenteUsername;
    }

    public int getDestinatario() {
        return destinatario;
    }

    public String getDestinatarioUsername() {
        return destinatarioUsername;
    }

    public int getOportunidad() {
        return oportunidad;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
