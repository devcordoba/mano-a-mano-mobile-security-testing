package com.ispc.manoamano.utilidades;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.ispc.manoamano.modelos.Mensaje;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MensajesNoLeidosTest {

    private final Gson gson = new Gson();

    /** Crea un Mensaje con el id indicado, igual que lo haría Retrofit con el JSON del servidor. */
    private Mensaje mensaje(int id) {
        return gson.fromJson("{\"id\": " + id + "}", Mensaje.class);
    }

    // ---------- contarNoLeidos ----------

    @Test
    public void contarNoLeidos_listaNull_devuelveCero() {
        assertEquals(0, MensajesNoLeidos.contarNoLeidos(null, 0));
    }

    @Test
    public void contarNoLeidos_todosMasNuevosQueElUltimoVisto_cuentaTodos() {
        List<Mensaje> recibidos = Arrays.asList(mensaje(3), mensaje(4), mensaje(5));
        assertEquals(3, MensajesNoLeidos.contarNoLeidos(recibidos, 0));
    }

    @Test
    public void contarNoLeidos_ningunoMasNuevo_devuelveCero() {
        List<Mensaje> recibidos = Arrays.asList(mensaje(3), mensaje(4));
        assertEquals(0, MensajesNoLeidos.contarNoLeidos(recibidos, 7));
    }

    @Test
    public void contarNoLeidos_mezclaDeVistosYNuevos_cuentaSoloLosNuevos() {
        List<Mensaje> recibidos = Arrays.asList(mensaje(5), mensaje(7), mensaje(8));
        assertEquals(1, MensajesNoLeidos.contarNoLeidos(recibidos, 7));
    }

    // ---------- idMasReciente ----------

    @Test
    public void idMasReciente_listaVacia_devuelveCero() {
        assertEquals(0, MensajesNoLeidos.idMasReciente(new ArrayList<>()));
    }

    @Test
    public void idMasReciente_listaDesordenada_devuelveElMayor() {
        List<Mensaje> recibidos = Arrays.asList(mensaje(7), mensaje(2), mensaje(9), mensaje(4));
        assertEquals(9, MensajesNoLeidos.idMasReciente(recibidos));
    }
}