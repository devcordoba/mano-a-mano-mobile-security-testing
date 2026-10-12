package com.ispc.manoamano.utilidades;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MensajeFormatoTest {

    // ---------- formatearFecha ----------

    @Test
    public void formatearFecha_conFechaIsoValida_devuelveDiaMesYHora() {
        String resultado = MensajeFormato.formatearFecha("2026-10-12T12:29:30.955219-03:00");
        assertEquals("12/10 12:29", resultado);
    }

    @Test
    public void formatearFecha_conNull_devuelveVacio() {
        assertEquals("", MensajeFormato.formatearFecha(null));
    }

    @Test
    public void formatearFecha_conTextoCorto_devuelveVacio() {
        assertEquals("", MensajeFormato.formatearFecha("2026-10-12"));
    }

    @Test
    public void formatearFecha_sinSeparadorT_devuelveVacio() {
        assertEquals("", MensajeFormato.formatearFecha("2026-10-12 12:29:30"));
    }

    // ---------- nombreVisible ----------

    @Test
    public void nombreVisible_conNull_devuelveUsuario() {
        assertEquals("Usuario", MensajeFormato.nombreVisible(null));
    }

    @Test
    public void nombreVisible_soloEspacios_devuelveUsuario() {
        assertEquals("Usuario", MensajeFormato.nombreVisible("   "));
    }

    @Test
    public void nombreVisible_conEspaciosAlrededor_devuelveNombreRecortado() {
        assertEquals("ana_voluntaria", MensajeFormato.nombreVisible("  ana_voluntaria "));
    }

    // ---------- validarCuerpo ----------

    @Test
    public void validarCuerpo_conNull_devuelveVacio() {
        assertEquals(MensajeFormato.CUERPO_VACIO, MensajeFormato.validarCuerpo(null));
    }

    @Test
    public void validarCuerpo_soloEspacios_devuelveVacio() {
        assertEquals(MensajeFormato.CUERPO_VACIO, MensajeFormato.validarCuerpo("    "));
    }

    @Test
    public void validarCuerpo_textoNormal_devuelveOk() {
        assertEquals(MensajeFormato.CUERPO_OK, MensajeFormato.validarCuerpo("Hola, ¿cómo estás?"));
    }

    @Test
    public void validarCuerpo_exactamenteElMaximo_devuelveOk() {
        String texto = repetir('a', MensajeFormato.MAX_CARACTERES_CUERPO);
        assertEquals(MensajeFormato.CUERPO_OK, MensajeFormato.validarCuerpo(texto));
    }

    @Test
    public void validarCuerpo_unoMasQueElMaximo_devuelveDemasiadoLargo() {
        String texto = repetir('a', MensajeFormato.MAX_CARACTERES_CUERPO + 1);
        assertEquals(MensajeFormato.CUERPO_DEMASIADO_LARGO, MensajeFormato.validarCuerpo(texto));
    }

    private static String repetir(char c, int veces) {
        StringBuilder sb = new StringBuilder(veces);
        for (int i = 0; i < veces; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}