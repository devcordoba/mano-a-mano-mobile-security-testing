package com.ispc.manoamano;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class ContactActivity extends AppCompatActivity {

    private static final String EMAIL_CONTACTO = "contacto@manoamano.com";
    private static final String TELEFONO_CONTACTO = "+5491100000000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        LinearLayout cardEmail = findViewById(R.id.cardEmail);
        LinearLayout cardTelefono = findViewById(R.id.cardTelefono);
        MaterialButton btnContactanos = findViewById(R.id.btnContactanos);
        MaterialButton btnEnviar = findViewById(R.id.btnEnviarContacto);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        EditText etNombre = findViewById(R.id.editNombreContacto);
        EditText etEmail = findViewById(R.id.editEmailContacto);
        EditText etAsunto = findViewById(R.id.editAsuntoContacto);
        EditText etMensaje = findViewById(R.id.editMensajeContacto);
        View miFormulario = findViewById(R.id.formularioContacto);

        configurarContactoEmail(cardEmail);
        configurarContactoTelefono(cardTelefono);
        configurarNavegacion(bottomNavigation);
        configurarBotonContactanos(btnContactanos, miFormulario);
        configurarBotonEnviarMensaje(btnEnviar, etNombre, etEmail, etAsunto, etMensaje, miFormulario);
    }

    private void configurarContactoEmail(LinearLayout cardEmail) {
        cardEmail.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + EMAIL_CONTACTO));

            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(
                        this,
                        "No hay una aplicación de correo disponible.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void configurarContactoTelefono(LinearLayout cardTelefono) {
        cardTelefono.setOnClickListener(view -> {
            Intent intent = new Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:" + TELEFONO_CONTACTO)
            );

            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(
                        this,
                        "No se puede abrir el marcador.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void configurarNavegacion(BottomNavigationView bottomNavigation) {
        bottomNavigation.setSelectedItemId(R.id.nav_contacto);

        bottomNavigation.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_inicio) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            }

            if (item.getItemId() == R.id.nav_contacto) {
                return true;
            }

            if (item.getItemId() == R.id.nav_perfil) {
                Intent intent = new Intent(this, PerfilActivity.class);
                startActivity(intent);
                return true;
            }

            return false;
        });
    }

    private void configurarBotonContactanos(MaterialButton btnContactanos, View miFormulario) {
        btnContactanos.setOnClickListener(view -> {
            // Alterna la visibilidad del formulario
            if (miFormulario.getVisibility() == View.GONE) {
                miFormulario.setVisibility(View.VISIBLE);
            } else {
                miFormulario.setVisibility(View.GONE);
            }
        });
    }

    private void configurarBotonEnviarMensaje(MaterialButton btnEnviar, EditText etNombre,
                                              EditText etEmail, EditText etAsunto,
                                              EditText etMensaje, View miFormulario) {
        btnEnviar.setOnClickListener(view -> {

            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String asunto = etAsunto.getText().toString().trim();
            String mensaje = etMensaje.getText().toString().trim();

            if (nombre.isEmpty()) {
                etNombre.setError("Por favor, ingresá tu nombre");
                etNombre.requestFocus();
                return;
            }

            if (nombre.length() < 3) {
                etNombre.setError("El nombre debe tener al menos 3 caracteres");
                etNombre.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                etEmail.setError("Por favor, ingresá tu email");
                etEmail.requestFocus();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Ingresá un email válido");
                etEmail.requestFocus();
                return;
            }

            if (asunto.isEmpty()) {
                etAsunto.setError("Por favor, ingresá un asunto");
                etAsunto.requestFocus();
                return;
            }

            if (asunto.length() < 5) {
                etAsunto.setError("El asunto debe tener al menos 5 caracteres");
                etAsunto.requestFocus();
                return;
            }

            if (mensaje.isEmpty()) {
                etMensaje.setError("Por favor, escribí un mensaje");
                etMensaje.requestFocus();
                return;
            }

            if (mensaje.length() < 10) {
                etMensaje.setError("El mensaje debe tener al menos 10 caracteres");
                etMensaje.requestFocus();
                return;
            }

            Toast.makeText(this, "¡Mensaje enviado con éxito! Nos contactaremos pronto.", Toast.LENGTH_LONG).show();

            etNombre.setText("");
            etEmail.setText("");
            etAsunto.setText("");
            etMensaje.setText("");

            miFormulario.setVisibility(View.GONE);
        });
    }
}