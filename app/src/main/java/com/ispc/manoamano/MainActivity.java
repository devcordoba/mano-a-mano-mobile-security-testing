package com.ispc.manoamano;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ispc.manoamano.adaptadores.OportunidadAdapter;
import com.ispc.manoamano.almacenamiento.SessionManager;
import com.ispc.manoamano.modelos.Causa;
import com.ispc.manoamano.modelos.Oportunidad;
import com.ispc.manoamano.modelos.TipoActividad;
import com.ispc.manoamano.repositorios.CausaRepository;
import com.ispc.manoamano.repositorios.AuthRepository;
import com.ispc.manoamano.repositorios.OportunidadRepository;
import com.ispc.manoamano.repositorios.TipoActividadRepository;
import com.ispc.manoamano.utilidades.ApiCallback;
import com.ispc.manoamano.modelos.LogoutResponse;
import com.ispc.manoamano.utilidades.NavegacionMensajes;
import com.ispc.manoamano.utilidades.NavegacionHelper;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_ABRIR_POSTULACIONES = "abrir_postulaciones";

    private final List<Oportunidad> todasLasOportunidades =
            new ArrayList<>();

    private final List<Oportunidad> oportunidadesVisibles =
            new ArrayList<>();

    private OportunidadAdapter adapter;

    private EditText editBuscar;

    private Spinner spinnerCausa;

    private Spinner spinnerActividad;

    private TextView textSinResultados;

    private ProgressBar progressOportunidades;

    // Evita mostrar "sin resultados" antes de que responda la API.
    private boolean catalogoCargado = false;

    private boolean cargando = false;

    private OportunidadRepository oportunidadRepository;

    private CausaRepository causaRepository;

    private TipoActividadRepository tipoActividadRepository;

    private SessionManager sessionManager;

    private AuthRepository authRepository;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());

    private Runnable pendingSearch;

    private Call<List<Oportunidad>> oportunidadCall;

    private static final int MODO_EXPLORAR = 0;
    private static final int MODO_USUARIO = 1;
    private static final int MODO_ORGANIZACION = 2;

    private int modoActual = MODO_EXPLORAR;
    private PanelUsuario panelUsuario;
    private PanelOrganizacion panelOrganizacion;
    private TextView btnModoUsuario;
    private TextView btnModoOrganizacion;
    private TextView tvTituloPrincipal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        editBuscar =
                findViewById(R.id.editBuscar);

        spinnerCausa =
                findViewById(R.id.spinnerCausa);

        spinnerActividad =
                findViewById(R.id.spinnerActividad);

        textSinResultados =
                findViewById(R.id.textSinResultados);

        progressOportunidades =
                findViewById(R.id.progressOportunidades);

        TextView tvBienvenida =
                findViewById(R.id.tvBienvenida);

        RecyclerView recyclerOportunidades =
                findViewById(R.id.recyclerOportunidades);

        TextView tvIniciarSesion =
                findViewById(R.id.tvIniciarSesion);

        sessionManager =
                new SessionManager(this);

        authRepository = new AuthRepository(this);

        oportunidadRepository =
                new OportunidadRepository(this);

        causaRepository =
                new CausaRepository(this);

        tipoActividadRepository =
                new TipoActividadRepository(this);

        configurarBienvenida(
                tvBienvenida,
                tvIniciarSesion
        );

        adapter =
                new OportunidadAdapter(
                        oportunidadesVisibles
                );

        recyclerOportunidades.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerOportunidades.setAdapter(adapter);

        configurarSpinners();

        configurarBuscador();

        configurarModo();

        configurarNavegacion();

        cargarCausas();

        cargarTiposActividad();

        cargarOportunidades();
        configurarBotonAyuda();
        abrirPostulacionesSiCorresponde();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        abrirPostulacionesSiCorresponde();
    }

    private void abrirPostulacionesSiCorresponde() {
        if (getIntent().getBooleanExtra(EXTRA_ABRIR_POSTULACIONES, false)
                && sessionManager.tieneSesion()) {
            mostrarModo(MODO_USUARIO);
        }
    }

    private void configurarBotonAyuda() {
        View fab = findViewById(R.id.fabAyuda);
        int umbral = ViewConfiguration.get(this).getScaledTouchSlop();
        fab.setOnTouchListener(new View.OnTouchListener() {
            float origenX;
            float origenY;
            float desplazamientoX;
            float desplazamientoY;
            boolean arrastrando;

            @Override
            public boolean onTouch(View vista, MotionEvent evento) {
                switch (evento.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        desplazamientoX = vista.getX() - evento.getRawX();
                        desplazamientoY = vista.getY() - evento.getRawY();
                        origenX = evento.getRawX();
                        origenY = evento.getRawY();
                        arrastrando = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float deltaX = evento.getRawX() - origenX;
                        float deltaY = evento.getRawY() - origenY;
                        if (!arrastrando && deltaX * deltaX + deltaY * deltaY > umbral * umbral) {
                            arrastrando = true;
                        }
                        if (arrastrando) {
                            View padre = (View) vista.getParent();
                            float x = evento.getRawX() + desplazamientoX;
                            float y = evento.getRawY() + desplazamientoY;
                            float maxX = padre.getWidth() - vista.getWidth();
                            float maxY = padre.getHeight() - vista.getHeight();
                            vista.setX(Math.max(0, Math.min(x, maxX)));
                            vista.setY(Math.max(0, Math.min(y, maxY)));
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (!arrastrando) {
                            vista.performClick();
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });
        fab.setOnClickListener(v -> mostrarAyuda());
    }

    private void mostrarAyuda() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View hoja = getLayoutInflater().inflate(R.layout.sheet_ayuda, null);
        LinearLayout contenedor = hoja.findViewById(R.id.contenedorSecciones);
        LayoutInflater inflater = getLayoutInflater();

        for (AyudaContenido.Seccion seccion : AyudaContenido.secciones()) {
            View item = inflater.inflate(R.layout.item_ayuda_seccion, contenedor, false);
            ((TextView) item.findViewById(R.id.tvAyudaTitulo)).setText(seccion.titulo);
            ((TextView) item.findViewById(R.id.tvAyudaCuerpo)).setText(seccion.cuerpo);
            contenedor.addView(item);
        }

        dialog.setContentView(hoja);
        dialog.setOnShowListener(d -> {
            FrameLayout fondo = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (fondo != null) {
                fondo.setBackgroundResource(android.R.color.transparent);
                BottomSheetBehavior.from(fondo).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        dialog.show();
    }

    private void configurarBienvenida(
            TextView tvBienvenida,
            TextView tvIniciarSesion
    ) {

        if (sessionManager.tieneSesion()) {

            String nombre =
                    sessionManager.obtenerFirstName();

            if (nombre != null && !nombre.trim().isEmpty()) {

                tvBienvenida.setText(
                        getString(
                                R.string.bienvenida_usuario,
                                nombre
                        )
                );

            } else {

                tvBienvenida.setText(
                        R.string.bienvenida_visitante
                );
            }

            tvIniciarSesion.setText(
                    "Cerrar sesión"
            );

            tvIniciarSesion.setOnClickListener(v -> {
                cerrarSesionRemota();
            });

        } else {

            tvBienvenida.setText(
                    R.string.bienvenida_visitante
            );

            tvIniciarSesion.setText(
                    "Iniciar sesión"
            );

            tvIniciarSesion.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                LoginActivity.class
                        );

                startActivity(intent);
            });
        }
    }

    private void cerrarSesionRemota() {
        authRepository.logout(new ApiCallback<LogoutResponse>(this) {
            @Override
            protected void onSuccess(LogoutResponse response) {
                finalizarCierreSesion();
            }

            @Override
            protected void onError(int codigo) {
                finalizarCierreSesion();
            }

            @Override
            protected void onNetworkError(Throwable t) {
                finalizarCierreSesion();
            }
        });
    }

    private void finalizarCierreSesion() {
        sessionManager.cerrarSesion();
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void configurarSpinners() {

        List<String> causas =
                new ArrayList<>();

        causas.add("Todas");

        ArrayAdapter<String> causasAdapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.item_spinner,
                        R.id.textSpinnerItem,
                        causas
                );

        causasAdapter.setDropDownViewResource(
                R.layout.item_spinner_dropdown
        );

        spinnerCausa.setAdapter(
                causasAdapter
        );

        List<String> actividades =
                new ArrayList<>();

        actividades.add("Todas");

        ArrayAdapter<String> actividadesAdapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.item_spinner,
                        R.id.textSpinnerItem,
                        actividades
                );

        actividadesAdapter.setDropDownViewResource(
                R.layout.item_spinner_dropdown
        );

        spinnerActividad.setAdapter(
                actividadesAdapter
        );

        AdapterView.OnItemSelectedListener listener =
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        aplicarFiltros();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                    }
                };

        spinnerCausa.setOnItemSelectedListener(
                listener
        );

        spinnerActividad.setOnItemSelectedListener(
                listener
        );
    }

    private void configurarBuscador() {

        editBuscar.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        if (pendingSearch != null) {
                            searchHandler.removeCallbacks(pendingSearch);
                        }
                        pendingSearch = () -> cargarOportunidades(
                                editBuscar.getText().toString().trim()
                        );
                        searchHandler.postDelayed(pendingSearch, 200);
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );
    }

    private void aplicarFiltros() {

        String causaElegida =
                spinnerCausa
                        .getSelectedItem()
                        .toString();

        String actividadElegida =
                spinnerActividad
                        .getSelectedItem()
                        .toString();

        oportunidadesVisibles.clear();

        for (Oportunidad oportunidad :
                todasLasOportunidades) {

            String nombreCausa = "";

            if (oportunidad.getCausa() != null) {

                nombreCausa =
                        oportunidad
                                .getCausa()
                                .getNombre();
            }

            String nombreActividad = "";

            if (oportunidad.getTipoActividad() != null) {

                nombreActividad =
                        oportunidad
                                .getTipoActividad()
                                .getNombre();
            }

            boolean coincideCausa =
                    causaElegida.equals("Todas")
                            || nombreCausa.equals(
                            causaElegida
                    );

            boolean coincideActividad =
                    actividadElegida.equals("Todas")
                            || nombreActividad.equals(
                            actividadElegida
                    );

            if (coincideCausa
                    && coincideActividad) {

                oportunidadesVisibles.add(
                        oportunidad
                );
            }
        }

        adapter.notifyDataSetChanged();

        boolean sinResultados = catalogoCargado
                && !cargando
                && oportunidadesVisibles.isEmpty()
                && modoActual == MODO_EXPLORAR;
        textSinResultados.setVisibility(sinResultados ? View.VISIBLE : View.GONE);
    }

    // Si se cancela una búsqueda no se oculta: la nueva llamada lo vuelve a mostrar.
    private void mostrarCargando(boolean cargando) {

        this.cargando = cargando;

        progressOportunidades.setVisibility(
                cargando ? View.VISIBLE : View.GONE
        );

        if (cargando) {
            textSinResultados.setVisibility(View.GONE);
        }
    }

    // Ante un error se vuelve a mostrar el estado de la lista para no dejarla en blanco.
    private void finalizarCargaConError() {

        mostrarCargando(false);

        catalogoCargado = true;

        aplicarFiltros();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NavegacionMensajes.actualizar(this, findViewById(R.id.bottomNavigation), R.id.nav_inicio);
    }

    private void configurarNavegacion() {
        BottomNavigationView bottomNavigation =
                findViewById(R.id.bottomNavigation);

        bottomNavigation.setSelectedItemId(R.id.nav_inicio);

        bottomNavigation.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.nav_inicio) {
                mostrarModo(MODO_EXPLORAR);
            }
        });

        bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            // Delegar la navegación de mensajes al componente compartido.
            if (NavegacionMensajes.manejarSeleccion(this, id)) {
                return true;
            }

            if (id == R.id.nav_contacto) {
                NavegacionHelper.irA(MainActivity.this, ContactActivity.class);
                return true;
            }

            if (id == R.id.nav_perfil) {
                NavegacionHelper.irA(MainActivity.this, PerfilActivity.class);
                return true;
            }

            if (id == R.id.nav_inicio) {
                mostrarModo(MODO_EXPLORAR);
                return true;
            }

            return true;
        });
    }

    private void configurarModo() {
        panelUsuario = new PanelUsuario(this);
        panelOrganizacion = new PanelOrganizacion(this);
        btnModoUsuario = findViewById(R.id.btnModoUsuario);
        btnModoOrganizacion = findViewById(R.id.btnModoOrganizacion);
        tvTituloPrincipal = findViewById(R.id.tvTituloPrincipal);

        View btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setVisibility(View.GONE);
        btnVolver.setOnClickListener(v -> mostrarModo(MODO_EXPLORAR));

        View grupoModo = findViewById(R.id.grupoModo);
        if (!sessionManager.tieneSesion()) {
            grupoModo.setVisibility(View.GONE);
            return;
        }

        grupoModo.setVisibility(View.VISIBLE);
        btnModoUsuario.setOnClickListener(v -> mostrarModo(modoActual == MODO_USUARIO ? MODO_EXPLORAR : MODO_USUARIO));
        btnModoOrganizacion.setOnClickListener(v -> mostrarModo(modoActual == MODO_ORGANIZACION ? MODO_EXPLORAR : MODO_ORGANIZACION));
    }

    private void mostrarModo(int modo) {
        modoActual = modo;
        boolean explorar = modo == MODO_EXPLORAR;
        findViewById(R.id.grupoExplorar).setVisibility(explorar ? View.VISIBLE : View.GONE);
        findViewById(R.id.grupoLista).setVisibility(explorar ? View.VISIBLE : View.GONE);
        tvTituloPrincipal.setVisibility(explorar ? View.VISIBLE : View.GONE);
        findViewById(R.id.btnVolver).setVisibility(explorar ? View.GONE : View.VISIBLE);
        findViewById(R.id.panelUsuario).setVisibility(modo == MODO_USUARIO ? View.VISIBLE : View.GONE);
        findViewById(R.id.panelOrganizacion).setVisibility(
                modo == MODO_ORGANIZACION ? View.VISIBLE : View.GONE
        );
        if (explorar) {
            aplicarFiltros();
        } else {
            textSinResultados.setVisibility(View.GONE);
        }
        pintarModo(btnModoUsuario, modo == MODO_USUARIO);
        pintarModo(btnModoOrganizacion, modo == MODO_ORGANIZACION);
        if (modo == MODO_USUARIO) {
            panelUsuario.cargar();
        } else if (modo == MODO_ORGANIZACION) {
            panelOrganizacion.cargar();
        }
    }

    private void pintarModo(TextView boton, boolean activo) {
        if (boton == null) {
            return;
        }
        boton.setBackgroundResource(activo ? R.drawable.bg_segmento_on : R.drawable.bg_segmento_off);
        boton.setTextColor(getColor(activo ? R.color.white : R.color.primary_dark));
    }

    private void cargarCausas() {

        causaRepository.obtenerCausas(
                new ApiCallback<List<Causa>>(this) {

                    @Override
                    protected void onSuccess(
                            List<Causa> causas
                    ) {

                        ArrayAdapter<String> adapterCausas =
                                (ArrayAdapter<String>)
                                        spinnerCausa.getAdapter();

                        adapterCausas.clear();

                        adapterCausas.add("Todas");

                        for (Causa causa : causas) {

                            adapterCausas.add(
                                    causa.getNombre()
                            );
                        }

                        adapterCausas.notifyDataSetChanged();
                    }
                }
        );
    }

    private void cargarTiposActividad() {

        tipoActividadRepository.obtenerTiposActividad(
                new ApiCallback<List<TipoActividad>>(this) {

                    @Override
                    protected void onSuccess(
                            List<TipoActividad> tiposActividad
                    ) {

                        ArrayAdapter<String> adapterActividades =
                                (ArrayAdapter<String>)
                                        spinnerActividad.getAdapter();

                        adapterActividades.clear();

                        adapterActividades.add("Todas");

                        for (
                                TipoActividad tipoActividad :
                                tiposActividad
                        ) {

                            adapterActividades.add(
                                    tipoActividad.getNombre()
                            );
                        }

                        adapterActividades.notifyDataSetChanged();
                    }
                }
        );
    }

    private void cargarOportunidades() {

        cargarOportunidades("");
    }

    private void cargarOportunidades(String busqueda) {

        if (oportunidadCall != null) {
            oportunidadCall.cancel();
        }

        mostrarCargando(true);

        oportunidadCall = oportunidadRepository.obtenerOportunidades(
                true,
                busqueda,
                new ApiCallback<List<Oportunidad>>(this) {

                    @Override
                    protected void onSuccess(
                            List<Oportunidad> oportunidades
                    ) {

                        mostrarCargando(false);

                        todasLasOportunidades.clear();

                        oportunidadesVisibles.clear();

                        if (oportunidades != null) {
                            todasLasOportunidades.addAll(oportunidades);
                        }

                        catalogoCargado = true;

                        aplicarFiltros();
                    }

                    @Override
                    protected void onError(int codigo) {
                        finalizarCargaConError();
                    }

                    @Override
                    protected void onNetworkError(Throwable t) {
                        finalizarCargaConError();
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        if (pendingSearch != null) {
            searchHandler.removeCallbacks(pendingSearch);
        }
        if (oportunidadCall != null) {
            oportunidadCall.cancel();
        }
        super.onDestroy();
    }
}
