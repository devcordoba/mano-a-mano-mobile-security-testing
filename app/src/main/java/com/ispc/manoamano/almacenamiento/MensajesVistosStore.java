package com.ispc.manoamano.almacenamiento;

import android.content.Context;
import android.content.SharedPreferences;

/** Recuerda, por usuario, el id del último mensaje recibido que se vio en la bandeja. */
public class MensajesVistosStore {

    private static final String PREFS_NAME = "manoamano_mensajes";
    private static final String CLAVE_ULTIMO_ID = "ultimo_id_visto_";

    private final SharedPreferences preferences;

    public MensajesVistosStore(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int obtenerUltimoIdVisto(int usuarioId) {
        return preferences.getInt(CLAVE_ULTIMO_ID + usuarioId, 0);
    }

    /** Solo avanza: nunca baja el valor guardado. */
    public void guardarUltimoIdVisto(int usuarioId, int idMensaje) {
        if (idMensaje > obtenerUltimoIdVisto(usuarioId)) {
            preferences.edit()
                    .putInt(CLAVE_ULTIMO_ID + usuarioId, idMensaje)
                    .apply();
        }
    }
}