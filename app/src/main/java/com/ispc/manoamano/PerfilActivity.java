package com.ispc.manoamano;

import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ispc.manoamano.almacenamiento.SessionManager;
import com.ispc.manoamano.modelos.PerfilResponse;
import com.ispc.manoamano.repositorios.AuthRepository;
import com.ispc.manoamano.utilidades.ApiCallback;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvNombreUsuario;
    private TextView tvEmail;

    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        tvEmail = findViewById(R.id.tvEmail);

        sessionManager = new SessionManager(this);
        authRepository = new AuthRepository(this);

        if (!sessionManager.tieneSesion()) {
            Toast.makeText(this, "Iniciá sesión para ver tu perfil.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        cargarPerfil();

    }

    private void cargarPerfil() {

        authRepository.perfil(
                new ApiCallback<PerfilResponse>(this) {

                    @Override
                    protected void onSuccess(
                            PerfilResponse response
                    ) {

                        if (response.getUser() == null) {
                            return;
                        }

                        String nombre =
                                response.getUser().getFirstName()
                                        + " "
                                        + response.getUser().getLastName();

                        tvNombreUsuario.setText(
                                nombre.trim()
                        );

                        tvEmail.setText(
                                response.getUser().getEmail()
                        );

                        String rol = response.getUser().getPerfil() == null
                                ? null
                                : response.getUser().getPerfil().getRol();

                        sessionManager.guardarUsuario(
                                response.getUser().getId(),
                                response.getUser().getUsername(),
                                response.getUser().getFirstName(),
                                response.getUser().getLastName(),
                                response.getUser().getEmail(),
                                rol
                        );
                    }
                }
        );
    }
}
