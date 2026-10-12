package com.ispc.manoamano;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

        ImageButton btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());
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
        oportunidadRepository.obtenerOportunidad(
                oportunidadId,
                new ApiCallback<Oportunidad>(this) {
                    @Override
                    protected void onSuccess(Oportunidad response) {
                        if (response == null) {
                            Toast.makeText(DetalleOportunidadActivity.this,
                                    "El servidor devolvió una respuesta vacía.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        oportunidad = response;
                        cargarDatos(response);
                        btnPostularse.setEnabled(response.isActiva());
                    }
                }
        );
    }

    private void postularse() {
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
        postulacionRepository.crear(
                new PostulacionRequest(oportunidad.getId()),
                new ApiCallback<Postulacion>(this) {
                    @Override
                    protected void onSuccess(Postulacion response) {
                        btnPostularse.setText("Postulación enviada");
                        Toast.makeText(DetalleOportunidadActivity.this,
                                "¡Postulación enviada con éxito!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onError(int codigo) {
                        btnPostularse.setEnabled(true);
                    }

                    @Override
                    protected void onNetworkError(Throwable t) {
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
