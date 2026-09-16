package com.ispc.manoamano;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegistroActivity extends AppCompatActivity {

    private EditText etUsuario;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etNombre;
    private EditText etApellido;

    private TextView etvIrLoginRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etUsuario = findViewById(R.id.ETnombre_usuario);
        etEmail = findViewById(R.id.ETemail);
        etPassword = findViewById(R.id.ETpassword);
        etNombre = findViewById(R.id.ETnombre);
        etApellido = findViewById(R.id.ETApellido);
        etvIrLoginRegistro = findViewById(R.id.tvIrLoginRegistro);

        Button btnRegistrar = findViewById(R.id.BTNregistrar);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());

        etvIrLoginRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);

                intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                );

                startActivity(intent);
                finish();
            }
        });

    }

    private void registrarUsuario() {

        String usuario = etUsuario.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();

        if (usuario.isEmpty()
                || email.isEmpty()
                || password.isEmpty()
                || nombre.isEmpty()
                || apellido.isEmpty()) {

            Toast.makeText(
                    this,
                    R.string.registro_campos_vacios,
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Datos válidos: continuar con el registro

        Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }
}