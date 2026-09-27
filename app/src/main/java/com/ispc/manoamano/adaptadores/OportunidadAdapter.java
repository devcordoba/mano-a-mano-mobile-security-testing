package com.ispc.manoamano.adaptadores;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ispc.manoamano.DetalleOportunidadActivity;
import com.ispc.manoamano.R;
import com.ispc.manoamano.api.RetrofitClient;
import com.ispc.manoamano.modelos.Oportunidad;

import java.util.List;

public class OportunidadAdapter
        extends RecyclerView.Adapter<OportunidadAdapter.OportunidadViewHolder> {

    private final List<Oportunidad> oportunidades;

    public OportunidadAdapter(
            List<Oportunidad> oportunidades
    ) {
        this.oportunidades = oportunidades;
    }

    @NonNull
    @Override
    public OportunidadViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_oportunidad,
                                parent,
                                false
                        );

        return new OportunidadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull OportunidadViewHolder holder,
            int position
    ) {

        Oportunidad oportunidad =
                oportunidades.get(position);

        Glide.with(holder.itemView.getContext())
                .load(RetrofitClient.normalizarUrlRecurso(oportunidad.getImagenUrl()))
                .placeholder(R.drawable.example_img1)
                .error(R.drawable.example_img1)
                .into(holder.imageOportunidad);

        holder.textTitulo.setText(
                oportunidad.getTitulo()
        );

        holder.textOrganizacion.setText(
                oportunidad.getOrganizacion() == null ? "" : oportunidad.getOrganizacion().getNombre()
        );

        holder.textUbicacion.setText(
                oportunidad.getUbicacion()
        );

        holder.textCausa.setText(
                oportunidad.getCausa() == null ? "" : oportunidad.getCausa().getNombre()
        );

        holder.textActividad.setText(
                oportunidad.getTipoActividad() == null ? "" : oportunidad.getTipoActividad().getNombre()
        );

        holder.textDisponibilidad.setText(
                oportunidad.getDisponibilidad()
        );

        holder.textCuposFecha.setText(
                holder.itemView
                        .getContext()
                        .getString(
                                R.string.cupos_y_fecha,
                                oportunidad.getCupos(),
                                oportunidad.getFechaActividad()
                        )
        );

        holder.textResumen.setText(
                oportunidad.getResumen()
        );

        holder.itemView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            v.getContext(),
                            DetalleOportunidadActivity.class
                    );

            intent.putExtra(
                    DetalleOportunidadActivity.EXTRA_OPORTUNIDAD_ID,
                    oportunidad.getId()
            );

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return oportunidades.size();
    }

    static class OportunidadViewHolder
            extends RecyclerView.ViewHolder {

        final ImageView imageOportunidad;

        final TextView textTitulo;
        final TextView textOrganizacion;
        final TextView textUbicacion;
        final TextView textCausa;
        final TextView textActividad;
        final TextView textDisponibilidad;
        final TextView textCuposFecha;
        final TextView textResumen;

        OportunidadViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            imageOportunidad =
                    itemView.findViewById(
                            R.id.imageOportunidad
                    );

            textTitulo =
                    itemView.findViewById(
                            R.id.textTitulo
                    );

            textOrganizacion =
                    itemView.findViewById(
                            R.id.textOrganizacion
                    );

            textUbicacion =
                    itemView.findViewById(
                            R.id.textUbicacion
                    );

            textCausa =
                    itemView.findViewById(
                            R.id.textCausa
                    );

            textActividad =
                    itemView.findViewById(
                            R.id.textActividad
                    );

            textDisponibilidad =
                    itemView.findViewById(
                            R.id.textDisponibilidad
                    );

            textCuposFecha =
                    itemView.findViewById(
                            R.id.textCuposFecha
                    );

            textResumen =
                    itemView.findViewById(
                            R.id.textResumen
                    );
        }
    }
}
