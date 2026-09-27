package com.ispc.manoamano;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.ispc.manoamano.almacenamiento.SessionManager;
import com.ispc.manoamano.modelos.MeResponse;
import com.ispc.manoamano.modelos.Usuario;
import com.ispc.manoamano.repositorios.AuthRepository;
import com.ispc.manoamano.utilidades.ApiCallback;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(
                new Runnable() {

                    @Override
                    public void run() {

                        validarSesionYAbrir();
                    }
                },
                SPLASH_DURATION
        );
    }

    private void validarSesionYAbrir() {
        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.tieneSesion()) {
            abrirPrincipal();
            return;
        }

        new AuthRepository(this).me(new ApiCallback<MeResponse>(this) {
            @Override
            protected void onSuccess(MeResponse response) {
                Usuario usuario = response == null ? null : response.getUser();
                if (usuario == null) {
                    sessionManager.cerrarSesion();
                } else {
                    String rol = usuario.getPerfil() == null ? null : usuario.getPerfil().getRol();
                    sessionManager.guardarUsuario(
                            usuario.getId(),
                            usuario.getUsername(),
                            usuario.getFirstName(),
                            usuario.getLastName(),
                            usuario.getEmail(),
                            rol
                    );
                }
                abrirPrincipal();
            }

            @Override
            protected void onError(int codigo) {
                sessionManager.cerrarSesion();
                abrirPrincipal();
            }

            @Override
            protected void onNetworkError(Throwable t) {
                abrirPrincipal();
            }
        });
    }

    private void abrirPrincipal() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
