package com.ispc.manoamano;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ispc.manoamano.adaptadores.MensajeAdapter;
import com.ispc.manoamano.almacenamiento.SessionManager;
import com.ispc.manoamano.modelos.BandejaMensajes;
import com.ispc.manoamano.modelos.Mensaje;
import com.ispc.manoamano.modelos.MensajeRequest;
import com.ispc.manoamano.repositorios.MensajeRepository;
import com.ispc.manoamano.utilidades.ApiCallback;
import com.ispc.manoamano.utilidades.MensajeFormato;

import java.util.List;

public class MensajesActivity extends AppCompatActivity
        implements MensajeAdapter.OnResponderListener {

    private static final int TAB_RECIBIDOS = 0;
    private static final int TAB_ENVIADOS = 1;

    private SessionManager sessionManager;
    private MensajeRepository mensajeRepository;
    private MensajeAdapter adapter;

    private TabLayout tabsMensajes;
    private RecyclerView recyclerMensajes;
    private ProgressBar progressMensajes;
    private TextView textVacioMensajes;

    /** Última bandeja recibida del servidor; null mientras no haya cargado. */
    private BandejaMensajes bandeja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // Sin sesión no hay mensajes: mandamos al Login (mismo patrón que PerfilActivity)
        if (!sessionManager.tieneSesion()) {
            irAlLogin();
            return;
        }

        setContentView(R.layout.activity_mensajes);

        mensajeRepository = new MensajeRepository(this);

        tabsMensajes = findViewById(R.id.tabsMensajes);
        recyclerMensajes = findViewById(R.id.recyclerMensajes);
        progressMensajes = findViewById(R.id.progressMensajes);
        textVacioMensajes = findViewById(R.id.textVacioMensajes);

        adapter = new MensajeAdapter(this);
        recyclerMensajes.setLayoutManager(new LinearLayoutManager(this));
        recyclerMensajes.setAdapter(adapter);

        tabsMensajes.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mostrarPestanaActual();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No hace falta
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No hace falta
            }
        });

        cargarBandeja();
    }

    private void cargarBandeja() {
        mostrarCargando(true);

        mensajeRepository.obtenerBandeja(
                sessionManager.obtenerUsuarioId(),
                new ApiCallback<BandejaMensajes>(this) {

                    @Override
                    protected void onSuccess(BandejaMensajes body) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        bandeja = body;
                        mostrarCargando(false);
                        mostrarPestanaActual();
                    }

                    @Override
                    protected void onError(int codigo) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        // 401 = sesión vencida (ApiCallback ya la borró)
                        if (codigo == 401) {
                            irAlLogin();
                            return;
                        }
                        mostrarCargando(false);
                        mostrarPestanaActual();
                    }

                    @Override
                    protected void onNetworkError(Throwable t) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        mostrarCargando(false);
                        mostrarPestanaActual();
                    }
                }
        );
    }

    /** Muestra la lista de la pestaña seleccionada, o el texto de bandeja vacía. */
    private void mostrarPestanaActual() {
        boolean recibidos = tabsMensajes.getSelectedTabPosition() == TAB_RECIBIDOS;

        List<Mensaje> lista = null;
        if (bandeja != null) {
            lista = recibidos ? bandeja.getRecibidos() : bandeja.getEnviados();
        }

        adapter.actualizar(lista, recibidos);

        boolean vacia = lista == null || lista.isEmpty();
        textVacioMensajes.setText(recibidos
                ? R.string.mensajes_vacio_recibidos
                : R.string.mensajes_vacio_enviados);
        textVacioMensajes.setVisibility(vacia ? View.VISIBLE : View.GONE);
        recyclerMensajes.setVisibility(vacia ? View.GONE : View.VISIBLE);
    }

    private void mostrarCargando(boolean cargando) {
        progressMensajes.setVisibility(cargando ? View.VISIBLE : View.GONE);
        if (cargando) {
            recyclerMensajes.setVisibility(View.GONE);
            textVacioMensajes.setVisibility(View.GONE);
        }
    }

    // ==========================================
    // RESPONDER (enviar mensaje)
    // ==========================================

    @Override
    public void onResponder(Mensaje mensaje) {
        View vistaDialogo = getLayoutInflater().inflate(R.layout.dialog_responder_mensaje, null);
        TextInputLayout inputLayout = vistaDialogo.findViewById(R.id.inputLayoutRespuesta);
        TextInputEditText editRespuesta = vistaDialogo.findViewById(R.id.editRespuesta);

        String nombre = MensajeFormato.nombreVisible(mensaje.getRemitenteUsername());

        AlertDialog dialogo = new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.mensajes_responder_titulo, nombre))
                .setView(vistaDialogo)
                .setNegativeButton(R.string.mensajes_cancelar, null)
                .setPositiveButton(R.string.mensajes_enviar, null) // el click se configura abajo
                .create();

        // Se reemplaza el click de "Enviar" para que el diálogo NO se cierre si el texto es inválido
        dialogo.setOnShowListener(d -> {
            Button btnEnviar = dialogo.getButton(AlertDialog.BUTTON_POSITIVE);
            btnEnviar.setOnClickListener(v -> {
                String texto = editRespuesta.getText() == null
                        ? ""
                        : editRespuesta.getText().toString();

                int resultado = MensajeFormato.validarCuerpo(texto);
                if (resultado == MensajeFormato.CUERPO_VACIO) {
                    inputLayout.setError(getString(R.string.mensajes_error_vacio));
                    return;
                }
                if (resultado == MensajeFormato.CUERPO_DEMASIADO_LARGO) {
                    inputLayout.setError(getString(R.string.mensajes_error_largo));
                    return;
                }

                inputLayout.setError(null);
                enviarRespuesta(mensaje, texto.trim(), dialogo, btnEnviar);
            });
        });

        dialogo.show();
    }

    private void enviarRespuesta(Mensaje original, String texto,
                                 AlertDialog dialogo, Button btnEnviar) {
        // Evita el doble envío si el usuario toca "Enviar" dos veces
        btnEnviar.setEnabled(false);

        MensajeRequest request = new MensajeRequest(
                original.getRemitente(),     // le respondemos a quien nos escribió
                original.getOportunidad(),   // misma oportunidad / conversación
                texto
        );

        mensajeRepository.enviar(request, new ApiCallback<Mensaje>(this) {

            @Override
            protected void onSuccess(Mensaje body) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dialogo.dismiss();
                Toast.makeText(MensajesActivity.this,
                        R.string.mensajes_enviado, Toast.LENGTH_SHORT).show();

                // Mostramos la pestaña Enviados y recargamos para ver la respuesta
                TabLayout.Tab tabEnviados = tabsMensajes.getTabAt(TAB_ENVIADOS);
                if (tabEnviados != null) {
                    tabEnviados.select();
                }
                cargarBandeja();
            }

            @Override
            protected void onError(int codigo) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                if (codigo == 401) {
                    dialogo.dismiss();
                    irAlLogin();
                    return;
                }
                // ApiCallback ya mostró el Toast; dejamos reintentar sin perder el texto
                btnEnviar.setEnabled(true);
            }

            @Override
            protected void onNetworkError(Throwable t) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                btnEnviar.setEnabled(true);
            }
        });
    }

    private void irAlLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}