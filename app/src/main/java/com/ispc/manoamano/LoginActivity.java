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
import com.ispc.manoamano.modelos.LoginRequest;
import com.ispc.manoamano.modelos.LoginResponse;
import com.ispc.manoamano.repositorios.AuthRepository;
import com.ispc.manoamano.utilidades.ApiCallback;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsuario;
    private EditText etPassword;
    private Button btnIngresar;
    private TextView tvIrRegistro;

    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsuario = findViewById(R.id.etUsuarioLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnIngresar = findViewById(R.id.btnIngresarLogin);
        tvIrRegistro = findViewById(R.id.tvIrRegistroLogin);

        authRepository = new AuthRepository(this);
        sessionManager = new SessionManager(this);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        tvIrRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(
                                LoginActivity.this,
                                RegistroActivity.class
                        )
                );
            }
        });
    }

    private void iniciarSesion() {

        String usuario = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                    this,
                    R.string.login_campos_vacios,
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        LoginRequest request = new LoginRequest(
                usuario,
                password
        );

        btnIngresar.setEnabled(false);

        authRepository.login(
                request,
                new ApiCallback<LoginResponse>(this) {

                    @Override
                    protected void onSuccess(LoginResponse response) {

                        if (response == null || response.getToken() == null || response.getUser() == null) {
                            btnIngresar.setEnabled(true);
                            Toast.makeText(LoginActivity.this,
                                    "El servidor devolvió una respuesta inválida.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String rol = response.getUser().getPerfil() == null
                                ? null
                                : response.getUser().getPerfil().getRol();

                        sessionManager.guardarToken(
                                response.getToken()
                        );

                        sessionManager.guardarUsuario(
                                response.getUser().getId(),
                                response.getUser().getUsername(),
                                response.getUser().getFirstName(),
                                response.getUser().getLastName(),
                                null,
                                rol
                        );

                        Intent intent = new Intent(
                                LoginActivity.this,
                                MainActivity.class
                        );

                        startActivity(intent);
                        finish();
                    }

                    @Override
                    protected void onError(int codigo) {
                        btnIngresar.setEnabled(true);
                    }

                    @Override
                    protected void onNetworkError(Throwable t) {
                        btnIngresar.setEnabled(true);
                    }
                }
        );
    }
}
