package com.ispc.manoamano.utilidades;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ispc.manoamano.MensajesActivity;
import com.ispc.manoamano.R;
import com.ispc.manoamano.almacenamiento.SessionManager;

/** Centraliza el ítem "Mensajes" del menú inferior para no repetir código en cada pantalla. */
public final class NavegacionMensajes {

    private NavegacionMensajes() {
        // Clase de utilidades: no se instancia
    }

    /**
     * Llamar en onResume(): muestra "Mensajes" solo si hay sesión
     * y vuelve a marcar como seleccionado el ítem de la pantalla actual.
     */
    public static void actualizar(Activity activity, BottomNavigationView nav, int idItemActual) {
        boolean haySesion = new SessionManager(activity).tieneSesion();

        MenuItem itemMensajes = nav.getMenu().findItem(R.id.nav_mensajes);
        if (itemMensajes != null) {
            itemMensajes.setVisible(haySesion);
        }

        MenuItem itemActual = nav.getMenu().findItem(idItemActual);
        if (itemActual != null) {
            itemActual.setChecked(true);
        }
    }

    /** Llamar dentro del listener del menú. Devuelve true si el ítem era "Mensajes". */
    public static boolean manejarSeleccion(Activity activity, int itemId) {
        if (itemId != R.id.nav_mensajes) {
            return false;
        }
        activity.startActivity(new Intent(activity, MensajesActivity.class));
        return true;
    }
}