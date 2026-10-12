package com.ispc.manoamano;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.ispc.manoamano.adaptadores.HiloMensajeAdapter;
import com.ispc.manoamano.adaptadores.MensajeAdapter;
import com.ispc.manoamano.almacenamiento.SessionManager;
import com.ispc.manoamano.modelos.BandejaMensajes;
import com.ispc.manoamano.modelos.Mensaje;
import com.ispc.manoamano.modelos.MensajeRequest;
import com.ispc.manoamano.modelos.Postulacion;
import com.ispc.manoamano.modelos.PostulacionBloque;
import com.ispc.manoamano.repositorios.MensajeRepository;
import com.ispc.manoamano.repositorios.PostulacionRepository;
import com.ispc.manoamano.utilidades.ApiCallback;
import com.ispc.manoamano.utilidades.MensajeFormato;
import com.ispc.manoamano.almacenamiento.MensajesVistosStore;
import com.ispc.manoamano.utilidades.MensajesNoLeidos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MensajesActivity extends AppCompatActivity
        implements MensajeAdapter.OnResponderListener {

    public static final String EXTRA_OTRO_ID = "hilo_otro";
    public static final String EXTRA_OPORTUNIDAD_ID = "hilo_oportunidad";
    public static final String EXTRA_TITULO = "hilo_titulo";

    private static final int TAB_RECIBIDOS = 0;
    private static final int TAB_ENVIADOS = 1;

    private SessionManager sessionManager;
    private MensajeRepository mensajeRepository;
    private MensajeAdapter adapter;
    private HiloMensajeAdapter hiloAdapter;

    private TabLayout tabsMensajes;
    private RecyclerView recyclerMensajes;
    private RecyclerView recyclerHilo;
    private ProgressBar progressMensajes;
    private ProgressBar progressHilo;
    private TextView textVacioMensajes;
    private TextView textVacioHilo;
    private LinearLayout grupoBandeja;
    private LinearLayout grupoHilo;
    private TextView textHiloTitulo;
    private TextView textHiloDetalle;
    private EditText editHilo;
    private boolean hiloAbierto;
    private int hiloOtroId;
    private int hiloOportunidadId;

    /** Última bandeja recibida del servidor; null mientras no haya cargado. */
    private BandejaMensajes bandeja;
    private final Map<Integer, String> titulosMensaje = new HashMap<>();
    private final Set<Integer> oportunidadesVoluntario = new HashSet<>();
    private final Set<Integer> oportunidadesOrganizacion = new HashSet<>();
    private final Map<Integer, Integer> propietarioOrganizacion = new HashMap<>();
    private final List<Postulacion> misPostulaciones = new ArrayList<>();
    private TextView btnEscribirPostulacion;

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
        recyclerHilo = findViewById(R.id.recyclerHilo);
        progressMensajes = findViewById(R.id.progressMensajes);
        progressHilo = findViewById(R.id.progressHilo);
        textVacioMensajes = findViewById(R.id.textVacioMensajes);
        textVacioHilo = findViewById(R.id.textVacioHilo);
        grupoBandeja = findViewById(R.id.grupoBandeja);
        grupoHilo = findViewById(R.id.grupoHilo);
        textHiloTitulo = findViewById(R.id.textHiloTitulo);
        textHiloDetalle = findViewById(R.id.textHiloDetalle);
        editHilo = findViewById(R.id.editHilo);
        btnEscribirPostulacion = findViewById(R.id.btnEscribirPostulacion);
        btnEscribirPostulacion.setOnClickListener(v -> elegirPostulacion());

        adapter = new MensajeAdapter(this);
        recyclerMensajes.setLayoutManager(new LinearLayoutManager(this));
        recyclerMensajes.setAdapter(adapter);

        LinearLayoutManager hiloLayout = new LinearLayoutManager(this);
        hiloLayout.setStackFromEnd(true);
        hiloAdapter = new HiloMensajeAdapter(sessionManager.obtenerUsuarioId());
        recyclerHilo.setLayoutManager(hiloLayout);
        recyclerHilo.setAdapter(hiloAdapter);

        findViewById(R.id.btnVolver).setOnClickListener(v -> {
            if (hiloAbierto) {
                cerrarHilo();
            } else {
                finish();
            }
        });
        findViewById(R.id.btnEnviarHilo).setOnClickListener(v -> enviarEnHilo());
        abrirHiloSiCorresponde();
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (hiloAbierto) {
                    cerrarHilo();
                    return;
                }
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

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
        cargarContexto();
    }

    private void cargarContexto() {
        PostulacionRepository postulaciones = new PostulacionRepository(this);
        postulaciones.obtenerPorVoluntario(
                sessionManager.obtenerUsuarioId(),
                new ApiCallback<List<Postulacion>>(this) {
                    @Override
                    protected void onSuccess(List<Postulacion> body) {
                        if (isFinishing() || body == null) {
                            return;
                        }
                        misPostulaciones.clear();
                        misPostulaciones.addAll(body);
                        for (Postulacion item : body) {
                            oportunidadesVoluntario.add(item.getOportunidad());
                            propietarioOrganizacion.put(item.getOportunidad(), item.getOrganizacionPropietario());
                            if (item.getOportunidadTitulo() != null && !item.getOportunidadTitulo().isEmpty()) {
                                titulosMensaje.put(item.getOportunidad(), item.getOportunidadTitulo());
                            }
                        }
                        publicarContexto();
                    }
                }
        );
        postulaciones.obtenerBloquesOrganizador(new ApiCallback<List<PostulacionBloque>>(this) {
            @Override
            protected void onSuccess(List<PostulacionBloque> body) {
                if (isFinishing() || body == null) {
                    return;
                }
                for (PostulacionBloque bloque : body) {
                    oportunidadesOrganizacion.add(bloque.getOportunidadId());
                    if (bloque.getTitulo() != null && !bloque.getTitulo().isEmpty()) {
                        titulosMensaje.put(bloque.getOportunidadId(), bloque.getTitulo());
                    }
                }
                publicarContexto();
            }
        });
    }

    private void publicarContexto() {
        if (adapter == null) {
            return;
        }
        adapter.setContexto(
                titulosMensaje,
                oportunidadesVoluntario,
                oportunidadesOrganizacion,
                propietarioOrganizacion
        );
        if (hiloAbierto) {
            textHiloDetalle.setText(detalleHilo(hiloOportunidadId, hiloOtroId));
        }
    }

    private void abrirHiloSiCorresponde() {
        int otro = getIntent().getIntExtra(EXTRA_OTRO_ID, -1);
        int oportunidad = getIntent().getIntExtra(EXTRA_OPORTUNIDAD_ID, -1);
        if (otro < 1 || oportunidad < 1) {
            return;
        }
        abrirConversacion(otro, oportunidad, getIntent().getStringExtra(EXTRA_TITULO));
    }

    private void abrirConversacion(int otroId, int oportunidadId, String titulo) {
        hiloOtroId = otroId;
        hiloOportunidadId = oportunidadId;
        hiloAbierto = true;
        grupoBandeja.setVisibility(View.GONE);
        grupoHilo.setVisibility(View.VISIBLE);
        textHiloTitulo.setText(titulo == null || titulo.trim().isEmpty() ? "Conversación" : titulo.trim());
        textHiloDetalle.setText(detalleHilo(hiloOportunidadId, hiloOtroId));
        mostrarCargandoHilo(bandeja == null);
        pintarHilo();
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
                        marcarRecibidosComoVistos();
                        mostrarCargando(false);
                        mostrarCargandoHilo(false);
                        mostrarPestanaActual();
                        if (hiloAbierto) {
                            pintarHilo();
                        }
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
                        mostrarCargandoHilo(false);
                        mostrarPestanaActual();
                    }

                    @Override
                    protected void onNetworkError(Throwable t) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        mostrarCargando(false);
                        mostrarCargandoHilo(false);
                        mostrarPestanaActual();
                    }
                }
        );
    }

    private void marcarRecibidosComoVistos() {
        if (bandeja == null) {
            return;
        }
        int idMasReciente = MensajesNoLeidos.idMasReciente(bandeja.getRecibidos());
        new MensajesVistosStore(this).guardarUltimoIdVisto(
                sessionManager.obtenerUsuarioId(), idMasReciente);
    }
    private void mostrarPestanaActual() {
        boolean recibidos = tabsMensajes.getSelectedTabPosition() == TAB_RECIBIDOS;

        List<Mensaje> lista = null;
        if (bandeja != null) {
            lista = recibidos ? bandeja.getRecibidos() : bandeja.getEnviados();
        }

        adapter.actualizar(lista, recibidos);
        btnEscribirPostulacion.setVisibility(recibidos ? View.GONE : View.VISIBLE);

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

    private void mostrarCargandoHilo(boolean cargando) {
        progressHilo.setVisibility(cargando ? View.VISIBLE : View.GONE);
        if (cargando) {
            textVacioHilo.setVisibility(View.GONE);
        }
    }

    // ==========================================
    // RESPONDER (enviar mensaje)
    // ==========================================

    private void elegirPostulacion() {
        if (misPostulaciones.isEmpty()) {
            Toast.makeText(this, "Todavía no te postulaste a ninguna oferta.", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] titulos = new String[misPostulaciones.size()];
        for (int i = 0; i < misPostulaciones.size(); i++) {
            String titulo = misPostulaciones.get(i).getOportunidadTitulo();
            titulos[i] = titulo == null || titulo.trim().isEmpty() ? "Postulación" : titulo.trim();
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle("Elegí una postulación")
                .setItems(titulos, (dialogo, indice) -> {
                    Postulacion item = misPostulaciones.get(indice);
                    abrirConversacion(
                            item.getOrganizacionPropietario(),
                            item.getOportunidad(),
                            titulos[indice]
                    );
                })
                .show();
    }

    @Override
    public void onAbrirHilo(Mensaje mensaje) {
        int yo = sessionManager.obtenerUsuarioId();
        boolean yoEnvie = mensaje.getRemitente() == yo;
        String nombre = MensajeFormato.nombreVisible(
                yoEnvie ? mensaje.getDestinatarioUsername() : mensaje.getRemitenteUsername()
        );
        abrirConversacion(
                yoEnvie ? mensaje.getDestinatario() : mensaje.getRemitente(),
                mensaje.getOportunidad(),
                nombre
        );
    }

    @Override
    public void onResponder(Mensaje mensaje) {
        onAbrirHilo(mensaje);
    }

    private void cerrarHilo() {
        hiloAbierto = false;
        grupoHilo.setVisibility(View.GONE);
        grupoBandeja.setVisibility(View.VISIBLE);
    }

    private void pintarHilo() {
        List<Mensaje> hilo = new ArrayList<>();
        if (bandeja != null) {
            Map<Integer, Mensaje> unicos = new HashMap<>();
            for (Mensaje mensaje : bandeja.getRecibidos()) {
                unicos.put(mensaje.getId(), mensaje);
            }
            for (Mensaje mensaje : bandeja.getEnviados()) {
                unicos.put(mensaje.getId(), mensaje);
            }
            int yo = sessionManager.obtenerUsuarioId();
            for (Mensaje mensaje : unicos.values()) {
                if (mensaje.getOportunidad() != hiloOportunidadId) {
                    continue;
                }
                boolean sale = mensaje.getRemitente() == yo && mensaje.getDestinatario() == hiloOtroId;
                boolean entra = mensaje.getRemitente() == hiloOtroId && mensaje.getDestinatario() == yo;
                if (sale || entra) {
                    hilo.add(mensaje);
                }
            }
            Collections.sort(hilo, (a, b) -> {
                String fechaA = a.getCreatedAt() == null ? "" : a.getCreatedAt();
                String fechaB = b.getCreatedAt() == null ? "" : b.getCreatedAt();
                return fechaA.compareTo(fechaB);
            });
        }
        hiloAdapter.mostrar(hilo);
        textVacioHilo.setVisibility(hilo.isEmpty() ? View.VISIBLE : View.GONE);
        if (!hilo.isEmpty()) {
            recyclerHilo.scrollToPosition(hilo.size() - 1);
        }
    }

    private String detalleHilo(int oportunidad, int otro) {
        boolean voluntario = oportunidadesVoluntario.contains(oportunidad);
        boolean organizacion = oportunidadesOrganizacion.contains(oportunidad);
        if (voluntario && organizacion) {
            Integer propietario = propietarioOrganizacion.get(oportunidad);
            voluntario = propietario != null && propietario == otro;
            organizacion = !voluntario;
        }
        String rol;
        if (voluntario) {
            rol = "Como voluntario, con la organización";
        } else if (organizacion) {
            rol = "Como organización, con el voluntario";
        } else {
            rol = "Conversación";
        }
        String titulo = titulosMensaje.get(oportunidad);
        if (titulo == null || titulo.trim().isEmpty()) {
            return rol;
        }
        return rol + " · " + titulo.trim();
    }

    private void enviarEnHilo() {
        String texto = editHilo.getText() == null ? "" : editHilo.getText().toString();
        int resultado = MensajeFormato.validarCuerpo(texto);
        if (resultado == MensajeFormato.CUERPO_VACIO) {
            Toast.makeText(this, R.string.mensajes_error_vacio, Toast.LENGTH_SHORT).show();
            return;
        }
        if (resultado == MensajeFormato.CUERPO_DEMASIADO_LARGO) {
            Toast.makeText(this, R.string.mensajes_error_largo, Toast.LENGTH_SHORT).show();
            return;
        }
        findViewById(R.id.btnEnviarHilo).setEnabled(false);
        mostrarCargandoHilo(true);
        mensajeRepository.enviar(
                new MensajeRequest(hiloOtroId, hiloOportunidadId, texto.trim()),
                new ApiCallback<Mensaje>(this) {
                    @Override
                    protected void onSuccess(Mensaje body) {
                        mostrarCargandoHilo(false);
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        editHilo.setText("");
                        findViewById(R.id.btnEnviarHilo).setEnabled(true);
                        cargarBandeja();
                    }

                    @Override
                    protected void onError(int codigo) {
                        mostrarCargandoHilo(false);
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        findViewById(R.id.btnEnviarHilo).setEnabled(true);
                        if (codigo == 401) {
                            irAlLogin();
                        }
                    }

                    @Override
                    protected void onNetworkError(Throwable t) {
                        mostrarCargandoHilo(false);
                        if (!isFinishing() && !isDestroyed()) {
                            findViewById(R.id.btnEnviarHilo).setEnabled(true);
                        }
                    }
                }
        );
    }

    private void irAlLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}