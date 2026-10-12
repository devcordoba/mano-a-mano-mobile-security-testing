package com.ispc.manoamano.utilidades;

public final class MensajeFormato {

    private MensajeFormato() {
        // Clase de utilidades: no se instancia
    }

    /**
     * Convierte "2026-10-11T19:44:12.123-03:00" en "11/10 19:44".
     * Si el texto es nulo o no tiene el formato esperado, devuelve "".
     */
    public static String formatearFecha(String fechaIso) {
        if (fechaIso == null || fechaIso.length() < 16 || fechaIso.charAt(10) != 'T') {
            return "";
        }
        String dia = fechaIso.substring(8, 10);
        String mes = fechaIso.substring(5, 7);
        String hora = fechaIso.substring(11, 16);
        return dia + "/" + mes + " " + hora;
    }

    /** Devuelve el nombre a mostrar, o "Usuario" si viene vacío. */
    public static String nombreVisible(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "Usuario";
        }
        return username.trim();
    }
}