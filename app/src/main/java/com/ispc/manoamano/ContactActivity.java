package com.ispc.manoamano;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.ispc.manoamano.modelos.ContactoRequest;
import com.ispc.manoamano.modelos.ContactoResponse;
import com.ispc.manoamano.repositorios.ContactoRepository;
import com.ispc.manoamano.utilidades.HttpErrorHandler;
import com.ispc.manoamano.utilidades.NavegacionMensajes; // NUEVO (1)
import com.ispc.manoamano.utilidades.NavegacionHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactActivity extends AppCompatActivity {

    private static final String EMAIL_CONTACTO = "contacto@manoamano.com";
    private static final String TELEFONO_CONTACTO = "+5491100000000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Volver.conectar(this);

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
        ProgressBar ruedita = findViewById(R.id.rueditaContacto);
        ScrollView scroll = findViewById(R.id.scrollContacto);

        configurarContactoEmail(cardEmail);
        configurarContactoTelefono(cardTelefono);
        configurarNavegacion(bottomNavigation);
        configurarBotonContactanos(btnContactanos, miFormulario, scroll, etNombre);
        configurarBotonEnviarMensaje(
                btnEnviar,
                btnContactanos,
                etNombre,
                etEmail,
                etAsunto,
                etMensaje,
                miFormulario,
                ruedita
        );
    }

    // NUEVO (2): al volver a esta pantalla, mostrar u ocultar "Mensajes" según la sesión
    @Override
    protected void onResume() {
        super.onResume();
        NavegacionMensajes.actualizar(this, findViewById(R.id.bottomNavigation), R.id.nav_contacto);
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

            // NUEVO (3): si tocaron "Mensajes", lo maneja NavegacionMensajes
            if (NavegacionMensajes.manejarSeleccion(this, item.getItemId())) {
                return true;
            }

            if (item.getItemId() == R.id.nav_inicio) {
                NavegacionHelper.irA(this, MainActivity.class);
                finish();
                return true;
            }

            if (item.getItemId() == R.id.nav_contacto) {
                return true;
            }

            if (item.getItemId() == R.id.nav_perfil) {
                NavegacionHelper.irA(this, PerfilActivity.class);
                return true;
            }

            return false;
        });
    }

    private void configurarBotonContactanos(MaterialButton btnContactanos, View miFormulario,
                                             ScrollView scroll, EditText etNombre) {
        btnContactanos.setOnClickListener(view -> {
            boolean abrir = miFormulario.getVisibility() != View.VISIBLE;
            miFormulario.setVisibility(abrir ? View.VISIBLE : View.GONE);
            btnContactanos.setText(abrir ? "OCULTAR FORMULARIO" : getString(R.string.contacto_boton));
            if (abrir) {
                miFormulario.post(() -> {
                    scroll.smoothScrollTo(0, miFormulario.getTop());
                    scroll.post(() -> {
                        scroll.smoothScrollTo(0, miFormulario.getTop());
                        etNombre.requestFocus();
                    });
                });
            }
        });
    }

    private void configurarBotonEnviarMensaje(MaterialButton btnEnviar, MaterialButton btnContactanos,
                                              EditText etNombre, EditText etEmail, EditText etAsunto,
                                              EditText etMensaje, View miFormulario, ProgressBar ruedita) {
        ContactoRepository contactoRepository = new ContactoRepository(this);
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

            btnEnviar.setEnabled(false);
            ruedita.setVisibility(View.VISIBLE);
            contactoRepository.crear(
                    new ContactoRequest(nombre, email, asunto, mensaje),
                    new Callback<ContactoResponse>() {
                        @Override
                        public void onResponse(Call<ContactoResponse> call, Response<ContactoResponse> response) {
                            if (isFinishing()) {
                                return;
                            }
                            btnEnviar.setEnabled(true);
                            ruedita.setVisibility(View.GONE);
                            if (!response.isSuccessful()) {
                                Toast.makeText(
                                        ContactActivity.this,
                                        HttpErrorHandler.obtenerMensaje(response.code()),
                                        Toast.LENGTH_LONG
                                ).show();
                                return;
                            }
                            Toast.makeText(
                                    ContactActivity.this,
                                    "¡Mensaje enviado con éxito! Nos contactaremos pronto.",
                                    Toast.LENGTH_LONG
                            ).show();
                            etNombre.setText("");
                            etEmail.setText("");
                            etAsunto.setText("");
                            etMensaje.setText("");
                            miFormulario.setVisibility(View.GONE);
                            btnContactanos.setText(R.string.contacto_boton);
                        }

                        @Override
                        public void onFailure(Call<ContactoResponse> call, Throwable t) {
                            if (isFinishing()) {
                                return;
                            }
                            btnEnviar.setEnabled(true);
                            ruedita.setVisibility(View.GONE);
                            Toast.makeText(
                                    ContactActivity.this,
                                    "No se pudo enviar el mensaje. Revisá tu conexión.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );
        });
    }
}