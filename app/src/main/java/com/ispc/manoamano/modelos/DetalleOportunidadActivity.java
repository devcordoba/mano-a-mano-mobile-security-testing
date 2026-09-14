package com.ispc.manoamano;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ispc.manoamano.modelos.Oportunidad;

public class DetalleOportunidadActivity extends AppCompatActivity {

    private ImageView imgDetalle;
    private TextView tvTitulo, tvOrganizacion, tvCausa, tvActividad;
    private TextView tvUbicacion, tvDisponibilidad, tvCuposFecha, tvResumen;
    private Button btnPostularse;
    private ImageButton btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_oportunidad);

        initViews();

        Oportunidad oportunidad = (Oportunidad) getIntent().getSerializableExtra("EXTRA_OPORTUNIDAD");

        if (oportunidad != null) {
            cargarDatos(oportunidad);
        }

        btnVolver.setOnClickListener(v -> finish());

        btnPostularse.setOnClickListener(v ->
                Toast.makeText(this, "¡Postulación enviada con éxito!", Toast.LENGTH_SHORT).show()
        );
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
        btnPostularse = findViewById(R.id.btnPostularse);
        btnVolver = findViewById(R.id.btnVolver);
    }

    private void cargarDatos(Oportunidad op) {
        imgDetalle.setImageResource(op.getImagenResId());
        tvTitulo.setText(op.getTitulo());
        tvOrganizacion.setText(op.getOrganizacion());
        tvCausa.setText(op.getCausa());
        tvActividad.setText(op.getTipoActividad());
        tvUbicacion.setText("Ubicación: " + op.getUbicacion());
        tvDisponibilidad.setText("⏱ Disponibilidad: " + op.getDisponibilidad());
        tvCuposFecha.setText(getString(R.string.cupos_y_fecha, op.getCupos(), op.getFechaActividad()));
        tvResumen.setText(op.getResumen());
    }
}