package com.ispc.manoamano.modelos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;

import org.junit.Test;

public class OportunidadTest {

    private final Gson gson = new Gson();

    @Test
    public void getResumen_cuandoExisteResumen_devuelveElResumen() {
        String json = "{\"titulo\":\"Reforestación\","
                + "\"descripcion\":\"Descripción completa de la actividad\","
                + "\"resumen\":\"Plantación de árboles\"}";

        Oportunidad oportunidad = gson.fromJson(json, Oportunidad.class);

        assertEquals("Plantación de árboles", oportunidad.getResumen());
    }

    @Test
    public void getResumen_cuandoNoExisteResumen_devuelveLaDescripcion() {
        String json = "{\"titulo\":\"Reforestación\","
                + "\"descripcion\":\"Descripción completa de la actividad\"}";

        Oportunidad oportunidad = gson.fromJson(json, Oportunidad.class);

        assertEquals(
                "Descripción completa de la actividad",
                oportunidad.getResumen()
        );
    }

    @Test
    public void parseo_cuandoElServidorDevuelveCamposDeOportunidad_mapeaLosDatos() {
        String json = "{\"id\":12,"
                + "\"titulo\":\"Apoyo escolar\","
                + "\"descripcion\":\"Acompañamiento educativo\","
                + "\"activa\":true,"
                + "\"cupos\":8,"
                + "\"imagen_url\":\"https://ejemplo.com/imagen.jpg\","
                + "\"updated_at\":\"2026-10-12T10:00:00Z\"}";

        Oportunidad oportunidad = gson.fromJson(json, Oportunidad.class);

        assertEquals(12, oportunidad.getId());
        assertEquals("Apoyo escolar", oportunidad.getTitulo());
        assertEquals("Acompañamiento educativo", oportunidad.getDescripcion());
        assertTrue(oportunidad.isActiva());
        assertEquals(8, oportunidad.getCupos());
        assertEquals(
                "https://ejemplo.com/imagen.jpg",
                oportunidad.getImagenUrl()
        );
        assertEquals("2026-10-12T10:00:00Z", oportunidad.getUpdatedAt());
    }
}
