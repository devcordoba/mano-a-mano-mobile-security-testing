package com.ispc.manoamano;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ispc.manoamano.api.RetrofitClient;
import com.ispc.manoamano.modelos.Oportunidad;
import com.ispc.manoamano.repositorios.OportunidadRepository;
import com.ispc.manoamano.utilidades.ApiCallback;

import retrofit2.Call;

public class DetalleOportunidadActivity extends AppCompatActivity {

    public static final String EXTRA_OPORTUNIDAD_ID = "EXTRA_OPORTUNIDAD_ID";

    private ImageView imgDetalle;
    private TextView tvTitulo, tvOrganizacion, tvCausa, tvActividad;
    private TextView tvUbicacion, tvDisponibilidad, tvCuposFecha, tvResumen, tvRequisitos;
    private Button btnPostularse;
    private ImageButton btnVolver;

    private OportunidadRepository oportunidadRepository;
    private Call<Oportunidad> detalleCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_oportunidad);

        initViews();
        oportunidadRepository = new OportunidadRepository(this);

        btnVolver.setOnClickListener(v -> finish());

        btnPostularse.setOnClickListener(v ->
                Toast.makeText(this, "¡Postulación enviada con éxito!", Toast.LENGTH_SHORT).show()
        );

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
        btnVolver = findViewById(R.id.btnVolver);
        // Se habilita cuando llega el detalle y la oportunidad sigue activa.
        btnPostularse.setEnabled(false);
    }

    private void cargarDetalle(int oportunidadId) {
        detalleCall = oportunidadRepository.obtenerOportunidad(
                oportunidadId,
                new ApiCallback<Oportunidad>(this) {
                    @Override
                    protected void onSuccess(Oportunidad oportunidad) {
                        if (oportunidad == null) {
                            Toast.makeText(DetalleOportunidadActivity.this,
                                    "El servidor devolvió una respuesta vacía.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        cargarDatos(oportunidad);
                        btnPostularse.setEnabled(oportunidad.isActiva());
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
        tvOrganizacion.setText(op.getNombreOrganizacion());
        tvCausa.setText(op.getNombreCausa());
        tvActividad.setText(op.getNombreTipoActividad());
        tvUbicacion.setText("Ubicación: " + texto(op.getUbicacion()));
        tvDisponibilidad.setText("⏱ Disponibilidad: " + texto(op.getDisponibilidad()));
        tvCuposFecha.setText(getString(R.string.cupos_y_fecha, op.getCupos(), texto(op.getFechaActividad())));
        tvResumen.setText(texto(op.getResumen()));

        String requisitos = texto(op.getRequisitos());
        tvRequisitos.setText("Requisitos: " + requisitos);
        tvRequisitos.setVisibility(requisitos.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }

    @Override
    protected void onDestroy() {
        if (detalleCall != null) {
            detalleCall.cancel();
        }
        super.onDestroy();
    }
}
