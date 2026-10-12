package com.ispc.manoamano.modelos;

import java.util.Collections;
import java.util.List;

public class BandejaMensajes {

    private List<Mensaje> recibidos;

    private List<Mensaje> enviados;

    /** Nunca devuelve null: si el backend no manda la lista, devuelve una vacía. */
    public List<Mensaje> getRecibidos() {
        return recibidos != null ? recibidos : Collections.emptyList();
    }

    /** Nunca devuelve null: si el backend no manda la lista, devuelve una vacía. */
    public List<Mensaje> getEnviados() {
        return enviados != null ? enviados : Collections.emptyList();
    }
}