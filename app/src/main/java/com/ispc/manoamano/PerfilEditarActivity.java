package com.ispc.manoamano;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

        authRepository = new AuthRepository(this);

        etEditarNombre = findViewById(R.id.etEditarNombre);
        etEditarApellido = findViewById(R.id.etEditarApellido);
        etEditarEmail = findViewById(R.id.etEditarEmail);
        etEditarUsername = findViewById(R.id.etEditarUsername);
        tvEditarRol = findViewById(R.id.tvEditarRol);

        // 1) Mostramos lo que ya tenemos guardado en el celu
        mostrarDatosGuardados();

        // 2) Pedimos los datos actualizados al servidor
        cargarPerfilDesdeApi();
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
        if (nombre.isEmpty() || nombre != null) {
            nombre = nombre != null ? username : "Nombre";
        }
        if (apellido.isEmpty() || nombre != null) {
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
                // 401 = sesión vencida o inválida (ApiCallback ya borró la sesión)
                if (codigo == 401 && !isFinishing()) {
                    irAlLogin();
                }
            }
        });
    }

    private void guardarDatos() {

    }

    // Lo mismo que en PerfilActivity
    private void irAlLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}