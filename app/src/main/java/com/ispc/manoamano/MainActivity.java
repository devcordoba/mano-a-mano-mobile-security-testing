package com.ispc.manoamano;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ispc.manoamano.adaptadores.OportunidadAdapter;
import com.ispc.manoamano.almacenamiento.SessionManager;
import com.ispc.manoamano.modelos.Causa;
import com.ispc.manoamano.modelos.LogoutResponse;
import com.ispc.manoamano.modelos.Oportunidad;
import com.ispc.manoamano.modelos.TipoActividad;
import com.ispc.manoamano.repositorios.AuthRepository;
import com.ispc.manoamano.repositorios.CausaRepository;
import com.ispc.manoamano.repositorios.OportunidadRepository;
import com.ispc.manoamano.repositorios.TipoActividadRepository;
import com.ispc.manoamano.utilidades.ApiCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {
    private static final String OPCION_TODAS = "Todas";
    private static final long DEMORA_BUSQUEDA_MS = 300;

    private final List<Oportunidad> todasLasOportunidades = new ArrayList<>();
    private final List<Oportunidad> oportunidadesVisibles = new ArrayList<>();
    private OportunidadAdapter adapter;
    private ArrayAdapter<String> causasAdapter;
    private ArrayAdapter<String> actividadesAdapter;
    private EditText editBuscar;
    private Spinner spinnerCausa;
    private Spinner spinnerActividad;
    private TextView textSinResultados;
    private ProgressBar progressOportunidades;
    private TextView tvBienvenida;
    private TextView tvIniciarSesion;

    private SessionManager sessionManager;
    private AuthRepository authRepository;
    private OportunidadRepository oportunidadRepository;
    private CausaRepository causaRepository;
    private TipoActividadRepository tipoActividadRepository;

    private final Handler busquedaHandler = new Handler(Looper.getMainLooper());
    private Runnable busquedaPendiente;
    private Call<List<Oportunidad>> oportunidadesCall;
    // Evita mostrar "sin resultados" antes de que responda la API.
    private boolean catalogoCargado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editBuscar = findViewById(R.id.editBuscar);
        spinnerCausa = findViewById(R.id.spinnerCausa);
        spinnerActividad = findViewById(R.id.spinnerActividad);
        textSinResultados = findViewById(R.id.textSinResultados);
        progressOportunidades = findViewById(R.id.progressOportunidades);
        tvBienvenida = findViewById(R.id.tvBienvenida);
        tvIniciarSesion = findViewById(R.id.tvIniciarSesion);
        RecyclerView recyclerOportunidades = findViewById(R.id.recyclerOportunidades);

        sessionManager = new SessionManager(this);
        authRepository = new AuthRepository(this);
        oportunidadRepository = new OportunidadRepository(this);
        causaRepository = new CausaRepository(this);
        tipoActividadRepository = new TipoActividadRepository(this);

        adapter = new OportunidadAdapter(oportunidadesVisibles);
        recyclerOportunidades.setLayoutManager(new LinearLayoutManager(this));
        recyclerOportunidades.setAdapter(adapter);

        configurarSpinners();
        configurarBuscador();
        configurarNavegacion();

        cargarCausas();
        cargarTiposActividad();
        cargarOportunidades("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // En onResume para que el encabezado se actualice al volver del login.
        configurarBienvenida();
    }

    private void configurarBienvenida() {
        if (!sessionManager.tieneSesion()) {
            tvBienvenida.setText(R.string.bienvenida_visitante);
            tvIniciarSesion.setText("Iniciar sesión");
            tvIniciarSesion.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            });
            return;
        }

        String nombre = sessionManager.obtenerFirstName();
        if (nombre == null || nombre.trim().isEmpty()) {
            nombre = sessionManager.obtenerUsername();
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            tvBienvenida.setText(R.string.bienvenida_visitante);
        } else {
            tvBienvenida.setText(getString(R.string.bienvenida_usuario, nombre));
        }
        tvIniciarSesion.setText("Cerrar sesión");
        tvIniciarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void cerrarSesion() {
        tvIniciarSesion.setEnabled(false);
        // Aunque falle el logout en el servidor, la sesión local se borra igual.
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
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void configurarSpinners() {
        // Arrancan solo con "Todas" y se completan con lo que devuelve la API.
        List<String> causas = new ArrayList<>();
        causas.add(OPCION_TODAS);
        causasAdapter = new ArrayAdapter<>(
                this, R.layout.item_spinner, R.id.textSpinnerItem, causas);
        causasAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerCausa.setAdapter(causasAdapter);

        List<String> actividades = new ArrayList<>();
        actividades.add(OPCION_TODAS);
        actividadesAdapter = new ArrayAdapter<>(
                this, R.layout.item_spinner, R.id.textSpinnerItem, actividades);
        actividadesAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerActividad.setAdapter(actividadesAdapter);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltros();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hace falta realizar ninguna acción.
            }
        };
        spinnerCausa.setOnItemSelectedListener(listener);
        spinnerActividad.setOnItemSelectedListener(listener);
    }

    private void configurarBuscador() {
        editBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Espera a que el usuario deje de escribir para no pedir a la API en cada tecla.
                if (busquedaPendiente != null) {
                    busquedaHandler.removeCallbacks(busquedaPendiente);
                }
                busquedaPendiente = () -> cargarOportunidades(editBuscar.getText().toString());
                busquedaHandler.postDelayed(busquedaPendiente, DEMORA_BUSQUEDA_MS);
            }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    // El texto se busca en el servidor (?q=); causa y actividad se filtran sobre lo recibido.
    private void aplicarFiltros() {
        String causaElegida = opcionElegida(spinnerCausa);
        String actividadElegida = opcionElegida(spinnerActividad);

        oportunidadesVisibles.clear();
        for (Oportunidad oportunidad : todasLasOportunidades) {
            boolean coincideCausa = causaElegida.equals(OPCION_TODAS)
                    || oportunidad.getNombreCausa().equals(causaElegida);
            boolean coincideActividad = actividadElegida.equals(OPCION_TODAS)
                    || oportunidad.getNombreTipoActividad().equals(actividadElegida);

            if (coincideCausa && coincideActividad) {
                oportunidadesVisibles.add(oportunidad);
            }
        }

        adapter.notifyDataSetChanged();
        textSinResultados.setVisibility(
                catalogoCargado && oportunidadesVisibles.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private String opcionElegida(Spinner spinner) {
        Object seleccion = spinner.getSelectedItem();
        return seleccion == null ? OPCION_TODAS : seleccion.toString();
    }

    private void configurarNavegacion() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_inicio);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // Revisa que 'nav_contacto' coincida exactamente con el id del item en tu bottom_navigation_menu.xml
            if (id == R.id.nav_contacto) {
                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(intent);
                return true;
            }
            if (id == R.id.nav_perfil) {
                Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
                startActivity(intent);
                return true;
            }
            // Si tienes otros fragmentos o pantallas que cambian dentro de la misma actividad,
            // puedes agregar los "else if" para nav_inicio u otras opciones aquí.

            return true;
        });
    }

    private void cargarCausas() {
        causaRepository.obtenerCausas(new ApiCallback<List<Causa>>(this) {
            @Override
            protected void onSuccess(List<Causa> causas) {
                causasAdapter.clear();
                causasAdapter.add(OPCION_TODAS);
                if (causas != null) {
                    for (Causa causa : causas) {
                        causasAdapter.add(causa.getNombre());
                    }
                }
                causasAdapter.notifyDataSetChanged();
            }
        });
    }

    private void cargarTiposActividad() {
        tipoActividadRepository.obtenerTiposActividad(new ApiCallback<List<TipoActividad>>(this) {
            @Override
            protected void onSuccess(List<TipoActividad> tiposActividad) {
                actividadesAdapter.clear();
                actividadesAdapter.add(OPCION_TODAS);
                if (tiposActividad != null) {
                    for (TipoActividad tipoActividad : tiposActividad) {
                        actividadesAdapter.add(tipoActividad.getNombre());
                    }
                }
                actividadesAdapter.notifyDataSetChanged();
            }
        });
    }

    private void cargarOportunidades(String busqueda) {
        // Si había una búsqueda en curso se cancela para que no pise a la más reciente.
        if (oportunidadesCall != null) {
            oportunidadesCall.cancel();
        }

        mostrarCargando(true);
        oportunidadesCall = oportunidadRepository.obtenerOportunidades(
                true,
                busqueda,
                new ApiCallback<List<Oportunidad>>(this) {
                    @Override
                    protected void onSuccess(List<Oportunidad> oportunidades) {
                        mostrarCargando(false);
                        todasLasOportunidades.clear();
                        if (oportunidades != null) {
                            todasLasOportunidades.addAll(oportunidades);
                        }
                        catalogoCargado = true;
                        aplicarFiltros();
                    }

                    @Override
                    protected void onError(int codigo) {
                        mostrarCargando(false);
                    }

                    @Override
                    protected void onNetworkError(Throwable t) {
                        mostrarCargando(false);
                    }
                }
        );
    }

    // Si se cancela una búsqueda no se oculta: la nueva llamada lo vuelve a mostrar.
    private void mostrarCargando(boolean cargando) {
        progressOportunidades.setVisibility(cargando ? View.VISIBLE : View.GONE);
        if (cargando) {
            textSinResultados.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        if (busquedaPendiente != null) {
            busquedaHandler.removeCallbacks(busquedaPendiente);
        }
        if (oportunidadesCall != null) {
            oportunidadesCall.cancel();
        }
        super.onDestroy();
    }
}
