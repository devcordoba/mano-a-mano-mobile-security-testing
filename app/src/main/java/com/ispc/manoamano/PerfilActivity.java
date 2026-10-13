package com.ispc.manoamano;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.ispc.manoamano.almacenamiento.SessionManager;
import com.ispc.manoamano.modelos.Perfil;
import com.ispc.manoamano.modelos.PerfilResponse;
import com.ispc.manoamano.modelos.Usuario;
import com.ispc.manoamano.repositorios.AuthRepository;
import com.ispc.manoamano.utilidades.ApiCallback;

import okhttp3.ResponseBody;

public class PerfilActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private AuthRepository authRepository;

    private TextView tvNombreUsuario;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvTelefono;
    private TextView tvIntereses;
    private TextView tvDisponibilidad;
    private ProgressBar ruedita;
    private TextView btnEliminarCuenta;
    private ScrollView scrollPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        if (!sessionManager.tieneSesion()) {
            Toast.makeText(this, "Iniciá sesión para ver tu perfil.", Toast.LENGTH_SHORT).show();
            irAlLogin();
            return;
        }

        setContentView(R.layout.activity_perfil);
        Volver.conectar(this);

        authRepository = new AuthRepository(this);

        scrollPerfil = findViewById(R.id.scrollPerfil);
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvTelefono = findViewById(R.id.tvTelefono);
        tvIntereses = findViewById(R.id.tvIntereses);
        tvDisponibilidad = findViewById(R.id.tvDisponibilidad);
        ruedita = findViewById(R.id.rueditaPerfil);
        AppCompatButton btnEditarDatos = findViewById(R.id.btnEditarDatos);
        btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta);

        btnEditarDatos.setOnClickListener(v ->
                startActivity(new Intent(this, PerfilEditarActivity.class))
        );
        btnEliminarCuenta.setOnClickListener(v -> mostrarConfirmacionEliminar());

        mostrarDatosGuardados();
        cargarPerfilDesdeApi();
    }

    private void mostrarCargaEliminar(boolean cargando) {
        ruedita.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btnEliminarCuenta.setEnabled(!cargando);
        btnEliminarCuenta.setAlpha(cargando ? 0.4f : 1f);
        if (cargando && scrollPerfil != null) {
            scrollPerfil.post(() -> scrollPerfil.fullScroll(View.FOCUS_DOWN));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager != null && sessionManager.tieneSesion()
                && findViewById(R.id.tvNombreUsuario) != null) {
            cargarPerfilDesdeApi();
        }
    }

    private void mostrarConfirmacionEliminar() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.eliminar_cuenta_titulo)
                .setMessage(R.string.eliminar_cuenta_mensaje)
                .setPositiveButton(R.string.eliminar_cuenta_ok, (dialog, id) -> eliminarCuenta())
                .setNegativeButton(R.string.eliminar_cuenta_cancelar, null)
                .show();
    }

    private void eliminarCuenta() {
        mostrarCargaEliminar(true);
        authRepository.eliminarCuenta(new ApiCallback<ResponseBody>(this) {
            @Override
            protected void onSuccess(ResponseBody body) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                sessionManager.cerrarSesion();
                Toast.makeText(
                        PerfilActivity.this,
                        R.string.perfil_cuenta_eliminada,
                        Toast.LENGTH_LONG
                ).show();
                Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            protected void onError(int codigo) {
                mostrarCargaEliminar(false);
                if (codigo == 401 && !isFinishing()) {
                    irAlLogin();
                    return;
                }
                Toast.makeText(
                        PerfilActivity.this,
                        "No se pudo eliminar la cuenta (código " + codigo + ").",
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            protected void onNetworkError(Throwable t) {
                mostrarCargaEliminar(false);
                Toast.makeText(
                        PerfilActivity.this,
                        "Error de red al eliminar la cuenta.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void mostrarDatosGuardados() {
        mostrarDatos(
                sessionManager.obtenerFirstName(),
                sessionManager.obtenerLastName(),
                sessionManager.obtenerUsername(),
                sessionManager.obtenerEmail(),
                null,
                null,
                null
        );
    }

    private void cargarPerfilDesdeApi() {
        authRepository.perfil(new ApiCallback<PerfilResponse>(this) {

            @Override
            protected void onSuccess(PerfilResponse body) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                if (body == null || body.getUser() == null) {
                    return;
                }

                Usuario usuario = body.getUser();
                Perfil perfil = usuario.getPerfil();
                String rol = perfil == null ? null : perfil.getRol();
                String telefono = perfil == null ? null : perfil.getTelefono();
                String intereses = perfil == null ? null : perfil.getInteresesCausas();
                String disponibilidad = perfil == null ? null : perfil.getDisponibilidadResumen();

                sessionManager.guardarUsuario(
                        usuario.getId(),
                        usuario.getUsername(),
                        usuario.getFirstName(),
                        usuario.getLastName(),
                        usuario.getEmail(),
                        rol
                );

                mostrarDatos(
                        usuario.getFirstName(),
                        usuario.getLastName(),
                        usuario.getUsername(),
                        usuario.getEmail(),
                        telefono,
                        intereses,
                        disponibilidad
                );
            }

            @Override
            protected void onError(int codigo) {
                if (codigo == 401 && !isFinishing()) {
                    irAlLogin();
                }
            }
        });
    }

    private void mostrarDatos(
            String nombre,
            String apellido,
            String username,
            String email,
            String telefono,
            String intereses,
            String disponibilidad
    ) {
        String nombreCompleto = ((nombre == null ? "" : nombre) + " "
                + (apellido == null ? "" : apellido)).trim();

        if (nombreCompleto.isEmpty()) {
            nombreCompleto = username != null && !username.isEmpty() ? username : "Usuario";
        }

        tvNombreUsuario.setText(nombreCompleto);
        tvUsername.setText(username != null && !username.isEmpty() ? "@" + username : "Sin usuario");
        tvEmail.setText(email != null && !email.isEmpty() ? email : "Sin email");
        tvTelefono.setText(textoOFallback(telefono, "-"));
        tvIntereses.setText(textoOFallback(intereses, "-"));
        tvDisponibilidad.setText(textoOFallback(disponibilidad, "-"));
    }

    private static String textoOFallback(String valor, String fallback) {
        return valor != null && !valor.trim().isEmpty() ? valor.trim() : fallback;
    }

    private void irAlLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
