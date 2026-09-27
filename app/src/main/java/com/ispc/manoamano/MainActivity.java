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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    private final List<Oportunidad> todasLasOportunidades =
            new ArrayList<>();

    private final List<Oportunidad> oportunidadesVisibles =
            new ArrayList<>();

    private OportunidadAdapter adapter;

    private EditText editBuscar;

    private Spinner spinnerCausa;

    private Spinner spinnerActividad;

    private TextView textSinResultados;

    private OportunidadRepository oportunidadRepository;

    private CausaRepository causaRepository;

    private TipoActividadRepository tipoActividadRepository;

    private SessionManager sessionManager;

    private AuthRepository authRepository;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());

    private Runnable pendingSearch;

    private Call<List<Oportunidad>> oportunidadCall;

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

        configurarNavegacion();

        cargarCausas();

        cargarTiposActividad();

        cargarOportunidades();
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
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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

        textSinResultados.setVisibility(
                oportunidadesVisibles.isEmpty()
                        ? View.VISIBLE
                        : View.GONE
        );
    }

    private void configurarNavegacion() {

        BottomNavigationView bottomNavigation =
                findViewById(
                        R.id.bottomNavigation
                );

        bottomNavigation.setSelectedItemId(
                R.id.nav_inicio
        );

        bottomNavigation.setOnItemSelectedListener(
                item -> {

                    int id =
                            item.getItemId();

                    if (id == R.id.nav_contacto) {

                        Intent intent =
                                new Intent(
                                        MainActivity.this,
                                        ContactActivity.class
                                );

                        startActivity(intent);

                        return true;
                    }

                    if (id == R.id.nav_perfil) {

                        Intent intent =
                                new Intent(
                                        MainActivity.this,
                                        PerfilActivity.class
                                );

                        startActivity(intent);

                        return true;
                    }

                    return true;
                }
        );
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

        oportunidadCall = oportunidadRepository.obtenerOportunidades(
                true,
                busqueda,
                new ApiCallback<List<Oportunidad>>(this) {

                    @Override
                    protected void onSuccess(
                            List<Oportunidad> oportunidades
                    ) {

                        todasLasOportunidades.clear();

                        oportunidadesVisibles.clear();

                        if (oportunidades != null) {
                            todasLasOportunidades.addAll(oportunidades);
                        }

                        aplicarFiltros();
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
