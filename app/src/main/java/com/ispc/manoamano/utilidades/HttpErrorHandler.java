package com.ispc.manoamano.utilidades;

public class HttpErrorHandler {

    private HttpErrorHandler() {
        // Evita crear instancias de esta clase.
    }

    public static String obtenerMensaje(int codigo) {

        switch (codigo) {

            case 400:
                return "La solicitud no es válida.";

            case 401:
                return "Tu sesión no es válida o ha expirado.";

            case 403:
                return "No tenés permisos para realizar esta acción.";

            case 404:
                return "No se encontró el recurso solicitado.";

            case 409:
                return "No se pudo completar la operación.";

            case 422:
                return "Los datos enviados no son válidos.";

            case 429:
                return "Demasiadas solicitudes. Intentá nuevamente más tarde.";

            case 500:
                return "Ocurrió un error en el servidor.";

            case 502:
            case 503:
            case 504:
                return "El servicio no está disponible en este momento.";

            default:
                return "Ocurrió un error. Intentá nuevamente.";
        }
    }
}