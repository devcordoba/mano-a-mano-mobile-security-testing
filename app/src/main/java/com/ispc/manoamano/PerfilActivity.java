package com.ispc.manoamano;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ispc.manoamano.almacenamiento.SessionManager;
import com.ispc.manoamano.modelos.PerfilResponse;
import com.ispc.manoamano.modelos.Usuario;
import com.ispc.manoamano.repositorios.AuthRepository;
import com.ispc.manoamano.utilidades.ApiCallback;

public class PerfilActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private AuthRepository authRepository;

    private TextView tvNombreUsuario;
    private TextView tvEmail;
    private TextView tvRol;

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

        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        tvEmail = findViewById(R.id.tvEmail);
        tvRol = findViewById(R.id.tvRol);
        Button btnEditarDatos = findViewById(R.id.btnEditarDatos);
        Button btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta);

        // Creamos un nuevo Dialog para eliminar cuentas
        AlertDialog.Builder alertaEliminar = new AlertDialog.Builder(this);
        alertaEliminar.setTitle(R.string.eliminar_cuenta_titulo);
        alertaEliminar.setMessage(R.string.eliminar_cuenta_mensaje);
        alertaEliminar.setPositiveButton(R.string.eliminar_cuenta_ok, (dialog, id) -> {
            // El usuario elimina su cuenta.
        });
        alertaEliminar.setNegativeButton(R.string.eliminar_cuenta_cancelar, (dialog, id) -> {
            // El usuario cancela.
        });

        AlertDialog dialog = alertaEliminar.create();

        // Hacer click en Editar perfil nos mueve hacia su pantalla
        btnEditarDatos.setOnClickListener(v -> irAEditarPerfil());

        // Hacer click en Eliminar cuenta nos muestra un dialog para confimar/cancelar
        btnEliminarCuenta.setOnClickListener(v -> dialog.show());

        mostrarDatosGuardados();
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

    private void mostrarDatos(String nombre, String apellido, String username,
                              String email, String rol) {

        String nombreCompleto = ((nombre == null ? "" : nombre) + " "
                + (apellido == null ? "" : apellido)).trim();

        if (nombreCompleto.isEmpty()) {
            nombreCompleto = username;
        }
        if (nombreCompleto == null || nombreCompleto.isEmpty()) {
            nombreCompleto = "Usuario";
        }

        tvNombreUsuario.setText(nombreCompleto);
        tvEmail.setText(email != null && !email.isEmpty() ? email : "Sin email");
        tvRol.setText("Rol: " + (rol != null && !rol.isEmpty() ? rol : "Sin rol"));
    }

    private void irAEditarPerfil() {
        Intent intent = new Intent(this, PerfilEditarActivity.class);
        startActivity(intent);
    }
    private void irAlLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
