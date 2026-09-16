package com.ispc.manoamano;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvNombreUsuario;
    private TextView tvEmail;
    private Button btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        tvEmail = findViewById(R.id.tvEmail);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Datos mock — se reemplazan por datos reales en el Sprint 2
        tvNombreUsuario.setText("Nombre Apellido");
        tvEmail.setText("usuario@ejemplo.com");

        btnCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}