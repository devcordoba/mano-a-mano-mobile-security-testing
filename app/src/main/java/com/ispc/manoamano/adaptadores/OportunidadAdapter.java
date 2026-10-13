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

        holder.textFecha.setText(
                formatearFecha(oportunidad.getFechaActividad())
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

    // La API manda "2026-11-08"; en la tarjeta alcanza con "08/11".
    private String formatearFecha(String fecha) {

        if (fecha == null) {
            return "";
        }

        String[] partes = fecha.split("-");

        if (partes.length != 3) {
            return fecha;
        }

        return partes[2] + "/" + partes[1];
    }

    static class OportunidadViewHolder
            extends RecyclerView.ViewHolder {

        final ImageView imageOportunidad;

        final TextView textTitulo;
        final TextView textOrganizacion;
        final TextView textUbicacion;
        final TextView textCausa;
        final TextView textFecha;

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

            textFecha =
                    itemView.findViewById(
                            R.id.textFecha
                    );
        }
    }
}
