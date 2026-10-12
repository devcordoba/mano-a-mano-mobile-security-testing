package com.ispc.manoamano;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ispc.manoamano.almacenamiento.SessionManager;
import com.ispc.manoamano.api.RetrofitClient;
import com.ispc.manoamano.modelos.Oportunidad;
import com.ispc.manoamano.modelos.Postulacion;
import com.ispc.manoamano.modelos.PostulacionRequest;
import com.ispc.manoamano.repositorios.OportunidadRepository;
import com.ispc.manoamano.repositorios.PostulacionRepository;
import com.ispc.manoamano.utilidades.ApiCallback;

import java.util.List;

public class DetalleOportunidadActivity extends AppCompatActivity {

    public static final String EXTRA_OPORTUNIDAD_ID = "EXTRA_OPORTUNIDAD_ID";

    private ImageView imgDetalle;
    private TextView tvTitulo;
    private TextView tvOrganizacion;
    private TextView tvCausa;
    private TextView tvActividad;
    private TextView tvUbicacion;
    private TextView tvDisponibilidad;
    private TextView tvCuposFecha;
    private TextView tvResumen;
    private TextView tvRequisitos;
    private Button btnPostularse;
    private Oportunidad oportunidad;
    private boolean yaPostulado;

    private OportunidadRepository oportunidadRepository;
    private PostulacionRepository postulacionRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_oportunidad);

        initViews();
        oportunidadRepository = new OportunidadRepository(this);
        postulacionRepository = new PostulacionRepository(this);
        sessionManager = new SessionManager(this);

        Volver.conectar(this);
        btnPostularse.setOnClickListener(v -> postularse());

        int oportunidadId = getIntent().getIntExtra(EXTRA_OPORTUNIDAD_ID, -1);
        if (oportunidadId < 1) {
            Toast.makeText(this, "No se pudo identificar la oportunidad.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cargarDetalle(oportunidadId);
    }

    private void initViews() {
        imgDetalle = findViewById(R.id.imgDetalle);
        tvTitulo = findViewById(R.id.tvDetalleTitulo);
        tvOrganizacion = findViewById(R.id.tvDetalleOrganizacion);
        tvCausa = findViewById(R.id.tvDetalleCausa);
        tvActividad = findViewById(R.id.tvDetalleActividad);
        tvUbicacion = findViewById(R.id.tvDetalleUbicacion);
        tvDisponibilidad = findViewById(R.id.tvDetalleDisponibilidad);
        tvCuposFecha = findViewById(R.id.tvDetalleCuposFecha);
        tvResumen = findViewById(R.id.tvDetalleResumen);
        tvRequisitos = findViewById(R.id.tvDetalleRequisitos);
        btnPostularse = findViewById(R.id.btnPostularse);
        btnPostularse.setEnabled(false);
    }

    private void cargarDetalle(int oportunidadId) {
        findViewById(R.id.rueditaDetalle).setVisibility(View.VISIBLE);
        oportunidadRepository.obtenerOportunidad(
                oportunidadId,
                new ApiCallback<Oportunidad>(this) {
                    @Override
                    protected void onSuccess(Oportunidad response) {
                        findViewById(R.id.rueditaDetalle).setVisibility(View.GONE);
                        if (response == null) {
                            Toast.makeText(DetalleOportunidadActivity.this,
                                    "El servidor devolvió una respuesta vacía.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        oportunidad = response;
                        cargarDatos(response);
                        if (yaPostulado) {
                            mostrarYaPostulado();
                        } else {
                            btnPostularse.setEnabled(response.isActiva());
                            revisarPostulacion();
                        }
                    }

                    @Override
                    protected void onError(int codigo) {
                        findViewById(R.id.rueditaDetalle).setVisibility(View.GONE);
                    }

                    @Override
                    protected void onNetworkError(Throwable t) {
                        findViewById(R.id.rueditaDetalle).setVisibility(View.GONE);
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (oportunidad != null && !yaPostulado) {
            revisarPostulacion();
        }
    }

    private void revisarPostulacion() {
        if (!sessionManager.tieneSesion() || oportunidad == null) {
            return;
        }
        int oportunidadId = oportunidad.getId();
        postulacionRepository.obtenerPorVoluntario(
                sessionManager.obtenerUsuarioId(),
                new ApiCallback<List<Postulacion>>(this) {
                    @Override
                    protected void onSuccess(List<Postulacion> body) {
                        if (isFinishing() || body == null) {
                            return;
                        }
                        for (Postulacion item : body) {
                            if (item.getOportunidad() == oportunidadId) {
                                mostrarYaPostulado();
                                return;
                            }
                        }
                    }
                }
        );
    }

    private void mostrarYaPostulado() {
        yaPostulado = true;
        btnPostularse.setText("Ya te postulaste. Ver postulaciones");
        btnPostularse.setEnabled(true);
    }

    private void irAPostulaciones() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_ABRIR_POSTULACIONES, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void postularse() {
        if (yaPostulado) {
            irAPostulaciones();
            return;
        }
        if (oportunidad == null) {
            return;
        }
        if (!sessionManager.tieneSesion()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Iniciá sesión para postularte.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPostularse.setEnabled(false);
        View rueda = findViewById(R.id.rueditaDetalle);
        rueda.bringToFront();
        rueda.setVisibility(View.VISIBLE);
        postulacionRepository.crear(
                new PostulacionRequest(oportunidad.getId()),
                new ApiCallback<Postulacion>(this) {
                    @Override
                    protected void onSuccess(Postulacion response) {
                        findViewById(R.id.rueditaDetalle).setVisibility(View.GONE);
                        mostrarYaPostulado();
                        Toast.makeText(DetalleOportunidadActivity.this,
                                "¡Postulación enviada con éxito!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onError(int codigo) {
                        findViewById(R.id.rueditaDetalle).setVisibility(View.GONE);
                        if (codigo == 400) {
                            mostrarYaPostulado();
                            return;
                        }
                        btnPostularse.setEnabled(oportunidad != null && oportunidad.isActiva());
                    }

                    @Override
                    protected void onNetworkError(Throwable t) {
                        findViewById(R.id.rueditaDetalle).setVisibility(View.GONE);
                        btnPostularse.setEnabled(true);
                    }
                }
        );
    }

    private void cargarDatos(Oportunidad op) {
        Glide.with(this)
                .load(RetrofitClient.normalizarUrlRecurso(op.getImagenUrl()))
                .placeholder(R.drawable.example_img1)
                .error(R.drawable.example_img1)
                .into(imgDetalle);

        tvTitulo.setText(texto(op.getTitulo()));
        tvOrganizacion.setText(op.getOrganizacion() == null ? "" : texto(op.getOrganizacion().getNombre()));
        tvCausa.setText(op.getCausa() == null ? "" : texto(op.getCausa().getNombre()));
        tvActividad.setText(op.getTipoActividad() == null ? "" : texto(op.getTipoActividad().getNombre()));
        tvUbicacion.setText("Ubicación: " + texto(op.getUbicacion()));
        tvDisponibilidad.setText("Disponibilidad: " + texto(op.getDisponibilidad()));
        tvCuposFecha.setText(getString(R.string.cupos_y_fecha, op.getCupos(), texto(op.getFechaActividad())));
        tvResumen.setText(texto(op.getResumen()));

        String requisitos = texto(op.getRequisitos());
        tvRequisitos.setText(requisitos);
        tvRequisitos.setVisibility(requisitos.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private String texto(String value) {
        return value == null ? "" : value;
    }
}
