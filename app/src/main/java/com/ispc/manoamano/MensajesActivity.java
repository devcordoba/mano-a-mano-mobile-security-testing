package com.ispc.manoamano;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.ispc.manoamano.adaptadores.MensajeAdapter;
import com.ispc.manoamano.almacenamiento.SessionManager;
import com.ispc.manoamano.modelos.BandejaMensajes;
import com.ispc.manoamano.modelos.Mensaje;
import com.ispc.manoamano.repositorios.MensajeRepository;
import com.ispc.manoamano.utilidades.ApiCallback;

import java.util.List;

public class MensajesActivity extends AppCompatActivity
        implements MensajeAdapter.OnResponderListener {

    private static final int TAB_RECIBIDOS = 0;

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

    @Override
    public void onResponder(Mensaje mensaje) {
        // Se implementa en el próximo paso (enviar mensajes)
        Toast.makeText(this, "Próximamente", Toast.LENGTH_SHORT).show();
    }

    private void irAlLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}