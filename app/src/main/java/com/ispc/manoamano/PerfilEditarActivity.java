package com.ispc.manoamano;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ispc.manoamano.almacenamiento.SessionManager;
import com.ispc.manoamano.modelos.PerfilResponse;
import com.ispc.manoamano.modelos.Usuario;
import com.ispc.manoamano.repositorios.AuthRepository;
import com.ispc.manoamano.utilidades.ApiCallback;

public class PerfilEditarActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private AuthRepository authRepository;
    private EditText etEditarNombre;
    private EditText etEditarApellido;
    private EditText etEditarUsername;
    private EditText etEditarEmail;
    private TextView tvEditarRol;
    private Button btnGuardarCambios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // Si no hay sesión iniciada, mandamos al Login
        if (!sessionManager.tieneSesion()) {
            irAlLogin();
            return;
        }

        setContentView(R.layout.activity_perfil_editar);
        Volver.conectar(this);

        authRepository = new AuthRepository(this);

        etEditarNombre = findViewById(R.id.etEditarNombre);
        etEditarApellido = findViewById(R.id.etEditarApellido);
        etEditarEmail = findViewById(R.id.etEditarEmail);
        etEditarUsername = findViewById(R.id.etEditarUsername);
        tvEditarRol = findViewById(R.id.tvEditarRol);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        // 1) Mostramos lo que ya tenemos guardado en el celu
        mostrarDatosGuardados();

        // 2) Pedimos los datos actualizados al servidor
        cargarPerfilDesdeApi();

        // 3) Establecemos el click listener para nuestro boton de guardar cambios
        btnGuardarCambios.setOnClickListener(v -> guardarDatos());
    }

    private void mostrarDatosGuardados() {
        String nombre = sessionManager.obtenerFirstName();
        String apellido = sessionManager.obtenerLastName();
        String username = sessionManager.obtenerUsername();

        mostrarDatos(
                nombre,
                apellido,
                username,
                sessionManager.obtenerEmail(),
                sessionManager.obtenerRol()
        );
    }

    private void mostrarDatos(String nombre, String apellido, String username,
                              String email, String rol) {

        // Validamos los datos que nos llegan desde el backend
        if (nombre.isEmpty()) {
            nombre = nombre != null ? username : "Nombre";
        }
        if (apellido.isEmpty()) {
            apellido = apellido != null ? username : "Apellido";
        }

        // Llenamos los campos correspondientes con los datos que nos llegaron
        etEditarNombre.setText(nombre);
        etEditarApellido.setText(apellido);
        etEditarEmail.setText(email != null && !email.isEmpty() ? email : "Sin email");
        etEditarUsername.setText(username != null && !username.isEmpty() ? username : "Usuario");
        tvEditarRol.setText("Rol: " + (rol != null && !rol.isEmpty() ? rol : "Sin rol"));
    }

    private void cargarPerfilDesdeApi() {
        findViewById(R.id.rueditaPerfil).setVisibility(View.VISIBLE);
        authRepository.perfil(new ApiCallback<PerfilResponse>(this) {

            @Override
            protected void onSuccess(PerfilResponse body) {
                findViewById(R.id.rueditaPerfil).setVisibility(View.GONE);
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                if (body == null || body.getUser() == null) {
                    return;
                }

                Usuario usuario = body.getUser();
                String rol = usuario.getPerfil() == null
                        ? null
                        : usuario.getPerfil().getRol();

                // Guardamos los datos actualizados (ahora sí con email)
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
                        rol
                );
            }

            @Override
            protected void onError(int codigo) {
                findViewById(R.id.rueditaPerfil).setVisibility(View.GONE);
                // 401 = sesión vencida o inválida (ApiCallback ya borró la sesión)
                if (codigo == 401 && !isFinishing()) {
                    irAlLogin();
                }
            }

            @Override
            protected void onNetworkError(Throwable t) {
                findViewById(R.id.rueditaPerfil).setVisibility(View.GONE);
            }
        });
    }

    private void guardarDatos() {
        String nombre = valor(etEditarNombre);
        String apellido = valor(etEditarApellido);
        String username = valor(etEditarUsername);
        String email = valor(etEditarEmail);

        if (nombre.isEmpty() || apellido.isEmpty() || username.isEmpty() || email.isEmpty()) {
            Toast.makeText(this,
                    "Por favor completa los campos vacios antes de guardar tus cambios.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Falta conectar con la API para hacer el recurso PATCH
        sessionManager.guardarUsuario(
                sessionManager.obtenerUsuarioId(),
                username,
                nombre,
                apellido,
                email,
                sessionManager.obtenerRol()
        );
    }

    // Lo mismo que en PerfilActivity
    private void irAlLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private static String valor(EditText edit) {
        return edit.getText() == null ? "" : edit.getText().toString().trim();
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor;
    }
}