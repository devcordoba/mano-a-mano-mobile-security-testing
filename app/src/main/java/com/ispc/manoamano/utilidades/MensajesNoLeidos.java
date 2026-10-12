package com.ispc.manoamano.utilidades;

import com.ispc.manoamano.modelos.Mensaje;

import java.util.List;

/** Cálculos sobre mensajes no leídos. Java puro: se testea sin emulador. */
public final class MensajesNoLeidos {

    private MensajesNoLeidos() {
        // Clase de utilidades: no se instancia
    }

    /** Cuenta los mensajes recibidos con id mayor al último que el usuario vio. */
    public static int contarNoLeidos(List<Mensaje> recibidos, int ultimoIdVisto) {
        if (recibidos == null) {
            return 0;
        }
        int cantidad = 0;
        for (Mensaje mensaje : recibidos) {
            if (mensaje != null && mensaje.getId() > ultimoIdVisto) {
                cantidad++;
            }
        }
        return cantidad;
    }

    /** Devuelve el id más alto de la lista (0 si está vacía). */
    public static int idMasReciente(List<Mensaje> recibidos) {
        int maximo = 0;
        if (recibidos == null) {
            return maximo;
        }
        for (Mensaje mensaje : recibidos) {
            if (mensaje != null && mensaje.getId() > maximo) {
                maximo = mensaje.getId();
            }
        }
        return maximo;
    }
}