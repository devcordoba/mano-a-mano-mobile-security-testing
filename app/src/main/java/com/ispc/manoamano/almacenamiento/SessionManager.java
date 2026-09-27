package com.ispc.manoamano.almacenamiento;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREFS_NAME = "manoamano_session";

    private static final String TOKEN = "token";
    private static final String USUARIO_ID = "usuario_id";
    private static final String USERNAME = "username";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String EMAIL = "email";
    private static final String ROL = "rol";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
        );
    }

    // ==========================================
    // TOKEN
    // ==========================================

    public void guardarToken(String token) {
        preferences.edit()
                .putString(TOKEN, token)
                .apply();
    }

    public String obtenerToken() {
        return preferences.getString(TOKEN, null);
    }

    public boolean tieneSesion() {
        String token = obtenerToken();

        return token != null && !token.isEmpty();
    }

    // ==========================================
    // USUARIO
    // ==========================================

    public void guardarUsuario(
            int id,
            String username,
            String firstName,
            String lastName,
            String email,
            String rol
    ) {
        preferences.edit()
                .putInt(USUARIO_ID, id)
                .putString(USERNAME, username)
                .putString(FIRST_NAME, firstName)
                .putString(LAST_NAME, lastName)
                .putString(EMAIL, email)
                .putString(ROL, rol)
                .apply();
    }

    public int obtenerUsuarioId() {
        return preferences.getInt(USUARIO_ID, -1);
    }

    public String obtenerUsername() {
        return preferences.getString(USERNAME, null);
    }

    public String obtenerFirstName() {
        return preferences.getString(FIRST_NAME, null);
    }

    public String obtenerLastName() {
        return preferences.getString(LAST_NAME, null);
    }

    public String obtenerEmail() {
        return preferences.getString(EMAIL, null);
    }

    public String obtenerRol() {
        return preferences.getString(ROL, null);
    }

    // ==========================================
    // CERRAR SESIÓN
    // ==========================================

    public void cerrarSesion() {
        preferences.edit()
                .clear()
                .apply();
    }
}
