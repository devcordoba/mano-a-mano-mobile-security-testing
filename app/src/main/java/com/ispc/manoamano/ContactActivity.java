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
        EditText etMensaje = findViewById(R.id.editMensajeContacto);
        View miFormulario = findViewById(R.id.formularioContacto);

        configurarContactoEmail(cardEmail);
        configurarContactoTelefono(cardTelefono);
        configurarNavegacion(bottomNavigation);
        configurarBotonContactanos(btnContactanos);
        configurarBotonEnviarMensaje(btnEnviar, etNombre, etMensaje, miFormulario);

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

            return false;
        });
    }

    private void configurarBotonContactanos(MaterialButton btnContactanos) {
        View miFormulario = findViewById(R.id.formularioContacto);

        btnContactanos.setOnClickListener(view -> {
            // Alterna la visibilidad del formulario
            if (miFormulario.getVisibility() == View.GONE) {
                miFormulario.setVisibility(View.VISIBLE);
            } else {
                miFormulario.setVisibility(View.GONE);
            }
        });
    }

    private void configurarBotonEnviarMensaje(MaterialButton btnEnviar, EditText etNombre, EditText etMensaje, View miFormulario) {
        btnEnviar.setOnClickListener(view -> {

            String nombre = etNombre.getText().toString().trim();
            String mensaje = etMensaje.getText().toString().trim();


            if (nombre.isEmpty()) {
                etNombre.setError("Por favor, ingresá tu nombre");
                return;
            }

            if (mensaje.isEmpty()) {
                etMensaje.setError("Por favor, escribí un mensaje");
                return;
            }


            Toast.makeText(this, "¡Mensaje enviado con éxito! Nos contactaremos pronto.", Toast.LENGTH_LONG).show();


            etNombre.setText("");
            etMensaje.setText("");


            miFormulario.setVisibility(View.GONE);
        });
    }

}