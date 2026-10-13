package com.ispc.manoamano.modelos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;

import org.junit.Test;

public class BandejaMensajesTest {

    private final Gson gson = new Gson();

    @Test
    public void getRecibidos_cuandoElServidorMandaNull_devuelveListaVacia() {
        BandejaMensajes bandeja = gson.fromJson(
                "{\"recibidos\": null, \"enviados\": []}", BandejaMensajes.class);

        assertNotNull(bandeja.getRecibidos());
        assertTrue(bandeja.getRecibidos().isEmpty());
    }

    @Test
    public void getters_cuandoFaltanLasClaves_devuelvenListasVacias() {
        BandejaMensajes bandeja = gson.fromJson("{}", BandejaMensajes.class);

        assertTrue(bandeja.getRecibidos().isEmpty());
        assertTrue(bandeja.getEnviados().isEmpty());
    }

    @Test
    public void parseo_conRespuestaRealDelBackend_mapeaTodosLosCampos() {
        // Estructura real devuelta por POST /api/mensajes/ (12/10/2026)
        String json = "{\"recibidos\": [{"
                + "\"id\": 7,"
                + "\"remitente\": 8,"
                + "\"remitente_username\": \"ana_voluntaria\","
                + "\"destinatario\": 1,"
                + "\"destinatario_username\": \"fundacion_manos\","
                + "\"oportunidad\": 1,"
                + "\"cuerpo\": \"Hola, mensaje de prueba\","
                + "\"created_at\": \"2026-10-12T12:29:30.955219-03:00\""
                + "}], \"enviados\": []}";

        BandejaMensajes bandeja = gson.fromJson(json, BandejaMensajes.class);
        Mensaje mensaje = bandeja.getRecibidos().get(0);

        assertEquals(1, bandeja.getRecibidos().size());
        assertEquals(7, mensaje.getId());
        assertEquals(8, mensaje.getRemitente());
        assertEquals("ana_voluntaria", mensaje.getRemitenteUsername());
        assertEquals("fundacion_manos", mensaje.getDestinatarioUsername());
        assertEquals("Hola, mensaje de prueba", mensaje.getCuerpo());
        assertEquals("2026-10-12T12:29:30.955219-03:00", mensaje.getCreatedAt());
    }
}