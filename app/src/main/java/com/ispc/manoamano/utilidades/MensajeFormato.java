package com.ispc.manoamano.utilidades;

public final class MensajeFormato {

    private MensajeFormato() {
        // Clase de utilidades: no se instancia
    }


    public static String formatearFecha(String fechaIso) {
        if (fechaIso == null || fechaIso.length() < 16 || fechaIso.charAt(10) != 'T') {
            return "";
        }
        String dia = fechaIso.substring(8, 10);
        String mes = fechaIso.substring(5, 7);
        String hora = fechaIso.substring(11, 16);
        return dia + "/" + mes + " " + hora;
    }

    public static String nombreVisible(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "Usuario";
        }
        return username.trim();
    }

    public static final int MAX_CARACTERES_CUERPO = 1000;

    public static final int CUERPO_OK = 0;
    public static final int CUERPO_VACIO = 1;
    public static final int CUERPO_DEMASIADO_LARGO = 2;

    public static int validarCuerpo(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return CUERPO_VACIO;
        }
        if (texto.trim().length() > MAX_CARACTERES_CUERPO) {
            return CUERPO_DEMASIADO_LARGO;
        }
        return CUERPO_OK;
    }
}