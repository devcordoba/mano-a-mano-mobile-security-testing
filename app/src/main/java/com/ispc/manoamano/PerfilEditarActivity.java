package com.ispc.manoamano;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ispc.manoamano.almacenamiento.SessionManager;
import com.ispc.manoamano.modelos.Perfil;
import com.ispc.manoamano.modelos.PerfilActualizacionRequest;
import com.ispc.manoamano.modelos.PerfilResponse;
import com.ispc.manoamano.modelos.Usuario;
import com.ispc.manoamano.repositorios.AuthRepository;
import com.ispc.manoamano.utilidades.ApiCallback;

public class PerfilEditarActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private AuthRepository authRepository;

    private TextView tvUsuarioFijo;
    private EditText etEditarNombre;
    private EditText etEditarApellido;
    private EditText etEditarEmail;
    private EditText etEditarTelefono;
    private EditText etEditarIntereses;
    private EditText etEditarDisponibilidad;
    private EditText etPasswordActual;
    private EditText etPasswordNueva;
    private ProgressBar ruedita;
    private Button btnGuardarCambios;
    private ScrollView scrollPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        if (!sessionManager.tieneSesion()) {
            irAlLogin();
            return;
        }

        setContentView(R.layout.activity_perfil_editar);
        Volver.conectar(this);

        authRepository = new AuthRepository(this);

        scrollPerfil = findViewById(R.id.scrollPerfilEditar);
        tvUsuarioFijo = findViewById(R.id.tvUsuarioFijo);
        etEditarNombre = findViewById(R.id.etEditarNombre);
        etEditarApellido = findViewById(R.id.etEditarApellido);
        etEditarEmail = findViewById(R.id.etEditarEmail);
        etEditarTelefono = findViewById(R.id.etEditarTelefono);
        etEditarIntereses = findViewById(R.id.etEditarIntereses);
        etEditarDisponibilidad = findViewById(R.id.etEditarDisponibilidad);
        etPasswordActual = findViewById(R.id.etPasswordActual);
        etPasswordNueva = findViewById(R.id.etPasswordNueva);
        ruedita = findViewById(R.id.rueditaPerfil);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        precargarDesdeSesion();
        cargarPerfilDesdeApi();
        btnGuardarCambios.setOnClickListener(v -> guardarDatos());
    }

    private void mostrarCarga(boolean cargando) {
        mostrarCarga(cargando, false);
    }

    private void mostrarCarga(boolean cargando, boolean bajarAlBoton) {
        ruedita.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btnGuardarCambios.setEnabled(!cargando);
        if (cargando && bajarAlBoton && scrollPerfil != null) {
            scrollPerfil.post(() -> scrollPerfil.fullScroll(View.FOCUS_DOWN));
        }
    }

    private void precargarDesdeSesion() {
        tvUsuarioFijo.setText(texto(sessionManager.obtenerUsername()));
        etEditarNombre.setText(texto(sessionManager.obtenerFirstName()));
        etEditarApellido.setText(texto(sessionManager.obtenerLastName()));
        etEditarEmail.setText(texto(sessionManager.obtenerEmail()));
    }

    private void cargarPerfilDesdeApi() {
        mostrarCarga(true);
        authRepository.perfil(new ApiCallback<PerfilResponse>(this) {

            @Override
            protected void onSuccess(PerfilResponse body) {
                mostrarCarga(false);
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                if (body == null || body.getUser() == null) {
                    return;
                }

                Usuario usuario = body.getUser();
                Perfil perfil = usuario.getPerfil();
                String rol = perfil == null ? null : perfil.getRol();

                sessionManager.guardarUsuario(
                        usuario.getId(),
                        usuario.getUsername(),
                        usuario.getFirstName(),
                        usuario.getLastName(),
                        usuario.getEmail(),
                        rol
                );

                tvUsuarioFijo.setText(texto(usuario.getUsername()));
                etEditarNombre.setText(texto(usuario.getFirstName()));
                etEditarApellido.setText(texto(usuario.getLastName()));
                etEditarEmail.setText(texto(usuario.getEmail()));
                etEditarTelefono.setText(perfil == null ? "" : texto(perfil.getTelefono()));
                etEditarIntereses.setText(perfil == null ? "" : texto(perfil.getInteresesCausas()));
                etEditarDisponibilidad.setText(
                        perfil == null ? "" : texto(perfil.getDisponibilidadResumen())
                );
            }

            @Override
            protected void onError(int codigo) {
                mostrarCarga(false);
                if (codigo == 401 && !isFinishing()) {
                    irAlLogin();
                }
            }

            @Override
            protected void onNetworkError(Throwable t) {
                mostrarCarga(false);
            }
        });
    }

    private void guardarDatos() {
        String nombre = valor(etEditarNombre);
        String apellido = valor(etEditarApellido);
        String email = valor(etEditarEmail);
        String telefono = valor(etEditarTelefono);
        String intereses = valor(etEditarIntereses);
        String disponibilidad = valor(etEditarDisponibilidad);
        String passwordActual = valor(etPasswordActual);
        String passwordNueva = valor(etPasswordNueva);

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Completá nombre, apellido y email.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Ingresá un email válido.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!passwordNueva.isEmpty() && passwordActual.isEmpty()) {
            Toast.makeText(
                    this,
                    "Para cambiar la contraseña indicá la actual.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        if (!passwordNueva.isEmpty() && !esPasswordRobusta(passwordNueva)) {
            etPasswordNueva.setError(getString(R.string.registro_password_debil));
            Toast.makeText(this, R.string.registro_password_debil, Toast.LENGTH_LONG).show();
            return;
        }

        String password = passwordNueva.isEmpty() ? null : passwordNueva;
        String passwordActualEnvio = password == null ? null : passwordActual;

        PerfilActualizacionRequest request = new PerfilActualizacionRequest(
                nombre,
                apellido,
                email,
                telefono,
                intereses,
                disponibilidad,
                password,
                passwordActualEnvio
        );

        mostrarCarga(true, true);
        authRepository.actualizarPerfil(request, new ApiCallback<PerfilResponse>(this) {
            @Override
            protected void onSuccess(PerfilResponse body) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                if (body != null && body.getUser() != null) {
                    Usuario usuario = body.getUser();
                    Perfil perfil = usuario.getPerfil();
                    sessionManager.guardarUsuario(
                            usuario.getId(),
                            usuario.getUsername(),
                            usuario.getFirstName(),
                            usuario.getLastName(),
                            usuario.getEmail(),
                            perfil == null ? sessionManager.obtenerRol() : perfil.getRol()
                    );
                }
                Toast.makeText(
                        PerfilEditarActivity.this,
                        R.string.perfil_actualizado,
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            }

            @Override
            protected void onError(int codigo) {
                mostrarCarga(false);
                if (codigo == 401 && !isFinishing()) {
                    irAlLogin();
                    return;
                }
                Toast.makeText(
                        PerfilEditarActivity.this,
                        "No se pudo actualizar el perfil (código " + codigo + ").",
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            protected void onNetworkError(Throwable t) {
                mostrarCarga(false);
                Toast.makeText(
                        PerfilEditarActivity.this,
                        "Error de red al guardar el perfil.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void irAlLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private static boolean esPasswordRobusta(String password) {
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*\\d.*")
                && password.matches(".*[^A-Za-z0-9].*");
    }

    private static String valor(EditText edit) {
        return edit.getText() == null ? "" : edit.getText().toString().trim();
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor;
    }
}
