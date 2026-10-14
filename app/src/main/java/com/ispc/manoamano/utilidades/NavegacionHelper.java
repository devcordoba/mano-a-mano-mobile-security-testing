package com.ispc.manoamano.utilidades;

import android.app.Activity;
import android.content.Intent;

/**
 * Abre pantallas del menú inferior sin duplicarlas en la pila de navegación.
 * CLEAR_TOP: si la pantalla ya está en la pila, vuelve a ella y cierra las de encima.
 * SINGLE_TOP: si ya es la pantalla de arriba, no crea otra copia.
 */
public final class NavegacionHelper {

    private NavegacionHelper() {
        // Clase de utilidades: no se instancia
    }

    public static void irA(Activity origen, Class<? extends Activity> destino) {
        Intent intent = new Intent(origen, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        origen.startActivity(intent);
    }
}
