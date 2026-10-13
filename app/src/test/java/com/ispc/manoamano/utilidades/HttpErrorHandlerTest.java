package com.ispc.manoamano.utilidades;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class HttpErrorHandlerTest {

    private static final String MENSAJE_POR_DEFECTO = "Ocurrió un error. Intentá nuevamente.";

    @Test
    public void codigo400_devuelveSolicitudNoValida() {
        assertEquals("La solicitud no es válida.", HttpErrorHandler.obtenerMensaje(400));
    }

    @Test
    public void codigo401_devuelveSesionExpirada() {
        assertEquals("Tu sesión no es válida o ha expirado.", HttpErrorHandler.obtenerMensaje(401));
    }

    @Test
    public void codigo403_devuelveSinPermisos() {
        assertEquals("No tenés permisos para realizar esta acción.", HttpErrorHandler.obtenerMensaje(403));
    }

    @Test
    public void codigo404_devuelveRecursoNoEncontrado() {
        assertEquals("No se encontró el recurso solicitado.", HttpErrorHandler.obtenerMensaje(404));
    }

    @Test
    public void codigo500_devuelveErrorDeServidor() {
        assertEquals("Ocurrió un error en el servidor.", HttpErrorHandler.obtenerMensaje(500));
    }

    @Test
    public void codigos502503504_devuelvenServicioNoDisponible() {
        String esperado = "El servicio no está disponible en este momento.";

        assertEquals(esperado, HttpErrorHandler.obtenerMensaje(502));
        assertEquals(esperado, HttpErrorHandler.obtenerMensaje(503));
        assertEquals(esperado, HttpErrorHandler.obtenerMensaje(504));
    }

    @Test
    public void codigoDesconocido_devuelveMensajePorDefecto() {
        assertEquals(MENSAJE_POR_DEFECTO, HttpErrorHandler.obtenerMensaje(418));
    }

    @Test
    public void codigoCero_devuelveMensajePorDefecto() {
        assertEquals(MENSAJE_POR_DEFECTO, HttpErrorHandler.obtenerMensaje(0));
    }

    @Test
    public void codigoNegativo_noDevuelveNuloYUsaMensajePorDefecto() {
        String mensaje = HttpErrorHandler.obtenerMensaje(-1);

        assertNotNull(mensaje);
        assertEquals(MENSAJE_POR_DEFECTO, mensaje);
    }
}
