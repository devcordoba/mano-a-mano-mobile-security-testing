package com.ispc.manoamano.almacenamiento;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SessionManagerTest {

    private static final String PREFS_NAME = "manoamano_session";

    private Context context;
    private SessionManager sessionManager;

    @Before
    public void setUp() {
        context = mock(Context.class);
        when(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE))
                .thenReturn(new FakeSharedPreferences());
        sessionManager = new SessionManager(context);
    }

    @Test
    public void constructor_usaElArchivoDePreferenciasPrivado() {
        verify(context).getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Test
    public void sinToken_noTieneSesion() {
        assertFalse(sessionManager.tieneSesion());
        assertNull(sessionManager.obtenerToken());
    }

    @Test
    public void guardarToken_tieneSesionYDevuelveElMismoToken() {
        sessionManager.guardarToken("abc.def.ghi");

        assertTrue(sessionManager.tieneSesion());
        assertEquals("abc.def.ghi", sessionManager.obtenerToken());
    }

    @Test
    public void tokenVacio_noCuentaComoSesion() {
        sessionManager.guardarToken("");

        assertFalse(sessionManager.tieneSesion());
    }

    @Test
    public void guardarToken_nuevoTokenReemplazaAlAnterior() {
        sessionManager.guardarToken("token-viejo");
        sessionManager.guardarToken("token-nuevo");

        assertEquals("token-nuevo", sessionManager.obtenerToken());
    }

    @Test
    public void sinUsuarioGuardado_devuelveValoresPorDefecto() {
        assertEquals(-1, sessionManager.obtenerUsuarioId());
        assertNull(sessionManager.obtenerUsername());
        assertNull(sessionManager.obtenerRol());
    }

    @Test
    public void guardarUsuario_devuelveTodosLosDatos() {
        sessionManager.guardarUsuario(
                7, "ana_voluntaria", "Ana", "Méndez",
                "ana.mendez@email.com", "voluntario");

        assertEquals(7, sessionManager.obtenerUsuarioId());
        assertEquals("ana_voluntaria", sessionManager.obtenerUsername());
        assertEquals("Ana", sessionManager.obtenerFirstName());
        assertEquals("Méndez", sessionManager.obtenerLastName());
        assertEquals("ana.mendez@email.com", sessionManager.obtenerEmail());
        assertEquals("voluntario", sessionManager.obtenerRol());
    }

    @Test
    public void cerrarSesion_borraTokenYDatosDelUsuario() {
        sessionManager.guardarToken("abc.def.ghi");
        sessionManager.guardarUsuario(
                7, "ana_voluntaria", "Ana", "Méndez",
                "ana.mendez@email.com", "voluntario");

        sessionManager.cerrarSesion();

        assertFalse(sessionManager.tieneSesion());
        assertNull(sessionManager.obtenerToken());
        assertEquals(-1, sessionManager.obtenerUsuarioId());
        assertNull(sessionManager.obtenerEmail());
    }

    @Test
    public void mantenerSesion_quedaDesactivadaSiNoSePide() {
        sessionManager.guardarToken("abc.def.ghi");

        assertFalse(sessionManager.mantieneSesion());
    }

    @Test
    public void guardarMantenerSesion_laRecuerdaYCerrarSesionLaBorra() {
        sessionManager.guardarMantenerSesion(true);

        assertTrue(sessionManager.mantieneSesion());

        sessionManager.cerrarSesion();

        assertFalse(sessionManager.mantieneSesion());
    }

    @Test
    public void sesionPersiste_alCrearOtroSessionManagerConElMismoContexto() {
        sessionManager.guardarToken("abc.def.ghi");

        SessionManager otraInstancia = new SessionManager(context);

        assertTrue(otraInstancia.tieneSesion());
        assertEquals("abc.def.ghi", otraInstancia.obtenerToken());
    }

    /**
     * SharedPreferences en memoria, solo para los tests.
     * Se comporta como la real, sin necesitar Android.
     */
    private static class FakeSharedPreferences implements SharedPreferences {

        private final Map<String, Object> datos = new HashMap<>();

        @Override
        public Map<String, ?> getAll() {
            return new HashMap<>(datos);
        }

        @Override
        public String getString(String key, String defValue) {
            return datos.containsKey(key) ? (String) datos.get(key) : defValue;
        }

        @Override
        public Set<String> getStringSet(String key, Set<String> defValues) {
            return defValues;
        }

        @Override
        public int getInt(String key, int defValue) {
            return datos.containsKey(key) ? (Integer) datos.get(key) : defValue;
        }

        @Override
        public long getLong(String key, long defValue) {
            return defValue;
        }

        @Override
        public float getFloat(String key, float defValue) {
            return defValue;
        }

        @Override
        public boolean getBoolean(String key, boolean defValue) {
            return datos.containsKey(key) ? (Boolean) datos.get(key) : defValue;
        }

        @Override
        public boolean contains(String key) {
            return datos.containsKey(key);
        }

        @Override
        public Editor edit() {
            return new FakeEditor();
        }

        @Override
        public void registerOnSharedPreferenceChangeListener(
                OnSharedPreferenceChangeListener listener) {
        }

        @Override
        public void unregisterOnSharedPreferenceChangeListener(
                OnSharedPreferenceChangeListener listener) {
        }

        private class FakeEditor implements Editor {

            private final Map<String, Object> pendientes = new HashMap<>();
            private boolean limpiar = false;

            @Override
            public Editor putString(String key, String value) {
                pendientes.put(key, value);
                return this;
            }

            @Override
            public Editor putStringSet(String key, Set<String> values) {
                return this;
            }

            @Override
            public Editor putInt(String key, int value) {
                pendientes.put(key, value);
                return this;
            }

            @Override
            public Editor putLong(String key, long value) {
                return this;
            }

            @Override
            public Editor putFloat(String key, float value) {
                return this;
            }

            @Override
            public Editor putBoolean(String key, boolean value) {
                pendientes.put(key, value);
                return this;
            }

            @Override
            public Editor remove(String key) {
                pendientes.remove(key);
                return this;
            }

            @Override
            public Editor clear() {
                limpiar = true;
                return this;
            }

            @Override
            public boolean commit() {
                if (limpiar) {
                    datos.clear();
                }
                datos.putAll(pendientes);
                return true;
            }

            @Override
            public void apply() {
                commit();
            }
        }
    }
}
