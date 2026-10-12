package com.ispc.manoamano.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.ispc.manoamano.R;
import com.ispc.manoamano.modelos.Mensaje;
import com.ispc.manoamano.utilidades.MensajeFormato;

import java.util.ArrayList;
import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {

    /** La Activity implementa esto para saber cuándo tocaron "Responder". */
    public interface OnResponderListener {
        void onResponder(Mensaje mensaje);
    }

    private final List<Mensaje> mensajes = new ArrayList<>();
    private final OnResponderListener listener;
    private boolean mostrandoRecibidos = true;

    public MensajeAdapter(OnResponderListener listener) {
        this.listener = listener;
    }

    /** Reemplaza la lista visible (al cargar la bandeja o al cambiar de pestaña). */
    public void actualizar(List<Mensaje> nuevos, boolean recibidos) {
        mensajes.clear();
        if (nuevos != null) {
            mensajes.addAll(nuevos);
        }
        mostrandoRecibidos = recibidos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mensaje, parent, false);
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);

        String nombre = mostrandoRecibidos
                ? MensajeFormato.nombreVisible(mensaje.getRemitenteUsername())
                : MensajeFormato.nombreVisible(mensaje.getDestinatarioUsername());

        holder.textContraparte.setText(holder.itemView.getContext().getString(
                mostrandoRecibidos ? R.string.mensajes_de : R.string.mensajes_para, nombre));
        holder.textFecha.setText(MensajeFormato.formatearFecha(mensaje.getCreatedAt()));
        holder.textCuerpo.setText(mensaje.getCuerpo());

        if (mostrandoRecibidos) {
            holder.btnResponder.setVisibility(View.VISIBLE);
            holder.btnResponder.setContentDescription(holder.itemView.getContext()
                    .getString(R.string.mensajes_responder_a, nombre));
            holder.btnResponder.setOnClickListener(v -> listener.onResponder(mensaje));
        } else {
            holder.btnResponder.setVisibility(View.GONE);
            holder.btnResponder.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class MensajeViewHolder extends RecyclerView.ViewHolder {
        final TextView textContraparte;
        final TextView textFecha;
        final TextView textCuerpo;
        final MaterialButton btnResponder;

        MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            textContraparte = itemView.findViewById(R.id.textContraparte);
            textFecha = itemView.findViewById(R.id.textFecha);
            textCuerpo = itemView.findViewById(R.id.textCuerpo);
            btnResponder = itemView.findViewById(R.id.btnResponder);
        }
    }
}