package com.ispc.manoamano;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // Clave con la que viaja el nombre de usuario hacia MainActivity
    public static final String EXTRA_USUARIO = "EXTRA_USUARIO";

    private EditText etUsuario;
    private EditText etPassword;
    private Button btnIngresar;
    private TextView tvIrRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsuario = findViewById(R.id.etUsuarioLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnIngresar = findViewById(R.id.btnIngresarLogin);
        tvIrRegistro = findViewById(R.id.tvIrRegistroLogin);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        // TODO: descomentar cuando se mergee feature/tomas-huespe-registro
        // tvIrRegistro.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //         startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
        //     }
        // });
    }

    private void iniciarSesion() {
        String usuario = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.login_campos_vacios, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(EXTRA_USUARIO, usuario);
        startActivity(intent);
        finish();
    }
}
