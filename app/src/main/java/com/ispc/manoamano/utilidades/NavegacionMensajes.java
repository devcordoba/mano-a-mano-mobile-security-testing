package com.ispc.manoamano.utilidades;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.core.content.ContextCompat;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ispc.manoamano.MensajesActivity;
import com.ispc.manoamano.R;
import com.ispc.manoamano.almacenamiento.MensajesVistosStore;
import com.ispc.manoamano.almacenamiento.SessionManager;
import com.ispc.manoamano.modelos.BandejaMensajes;
import com.ispc.manoamano.repositorios.MensajeRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Centraliza el ítem "Mensajes" del menú inferior para no repetir código en cada pantalla. */
public final class NavegacionMensajes {

    private NavegacionMensajes() {
        // Clase de utilidades: no se instancia
    }

    /**
     * Llamar en onResume(): muestra "Mensajes" solo si hay sesión,
     * actualiza el contador de no leídos y marca el ítem de la pantalla actual.
     */
    public static void actualizar(Activity activity, BottomNavigationView nav, int idItemActual) {
        SessionManager sessionManager = new SessionManager(activity);
        boolean haySesion = sessionManager.tieneSesion();

        MenuItem itemMensajes = nav.getMenu().findItem(R.id.nav_mensajes);
        if (itemMensajes != null) {
            itemMensajes.setVisible(haySesion);
        }

        MenuItem itemActual = nav.getMenu().findItem(idItemActual);
        if (itemActual != null) {
            itemActual.setChecked(true);
        }

        if (haySesion) {
            actualizarContadorNoLeidos(activity, nav, sessionManager.obtenerUsuarioId());
        } else {
            nav.removeBadge(R.id.nav_mensajes);
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

    /**
     * Pide la bandeja en segundo plano y muestra la cantidad de mensajes nuevos.
     * Usa un Callback silencioso (no ApiCallback): si falla, no mostramos Toasts
     * porque el contador es información secundaria.
     */
    private static void actualizarContadorNoLeidos(Activity activity,
                                                   BottomNavigationView nav,
                                                   int usuarioId) {
        new MensajeRepository(activity).obtenerBandeja(usuarioId, new Callback<BandejaMensajes>() {
            @Override
            public void onResponse(Call<BandejaMensajes> call, Response<BandejaMensajes> response) {
                if (activity.isFinishing() || activity.isDestroyed()) {
                    return;
                }
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }
                int ultimoVisto = new MensajesVistosStore(activity).obtenerUltimoIdVisto(usuarioId);
                int noLeidos = MensajesNoLeidos.contarNoLeidos(
                        response.body().getRecibidos(), ultimoVisto);
                mostrarContador(nav, noLeidos);
            }

            @Override
            public void onFailure(Call<BandejaMensajes> call, Throwable t) {
                // Silencioso: si no hay red, simplemente no se muestra el contador
            }
        });
    }

    private static void mostrarContador(BottomNavigationView nav, int cantidad) {
        if (cantidad <= 0) {
            nav.removeBadge(R.id.nav_mensajes);
            return;
        }
        BadgeDrawable badge = nav.getOrCreateBadge(R.id.nav_mensajes);
        badge.setVisible(true);
        badge.setNumber(cantidad);
        badge.setBackgroundColor(ContextCompat.getColor(nav.getContext(), R.color.primary));
    }
}