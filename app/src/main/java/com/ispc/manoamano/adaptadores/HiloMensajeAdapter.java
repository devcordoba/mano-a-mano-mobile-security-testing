package com.ispc.manoamano.adaptadores;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ispc.manoamano.R;
import com.ispc.manoamano.modelos.Mensaje;
import com.ispc.manoamano.utilidades.MensajeFormato;

import java.util.ArrayList;
import java.util.List;

public class HiloMensajeAdapter extends RecyclerView.Adapter<HiloMensajeAdapter.Holder> {

    private final List<Mensaje> mensajes = new ArrayList<>();
    private final int usuarioId;

    public HiloMensajeAdapter(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void mostrar(List<Mensaje> nuevos) {
        mensajes.clear();
        if (nuevos != null) {
            mensajes.addAll(nuevos);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_burbuja_mensaje, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Mensaje mensaje = mensajes.get(position);
        boolean mio = mensaje.getRemitente() == usuarioId;

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.burbuja.getLayoutParams();
        params.gravity = mio ? Gravity.END : Gravity.START;
        holder.burbuja.setLayoutParams(params);
        holder.burbuja.setBackgroundResource(mio ? R.drawable.bg_burbuja_mia : R.drawable.bg_burbuja_otro);

        int colorTexto = holder.itemView.getContext().getColor(mio ? R.color.white : R.color.text_primary);
        int colorSuave = holder.itemView.getContext().getColor(mio ? R.color.gold_light : R.color.text_secondary);
        holder.autor.setTextColor(colorTexto);
        holder.cuerpo.setTextColor(colorTexto);
        holder.fecha.setTextColor(colorSuave);

        if (mio) {
            String para = MensajeFormato.nombreVisible(mensaje.getDestinatarioUsername());
            holder.autor.setText("Vos → " + para);
        } else {
            holder.autor.setText(MensajeFormato.nombreVisible(mensaje.getRemitenteUsername()));
        }
        holder.cuerpo.setText(mensaje.getCuerpo() == null ? "" : mensaje.getCuerpo());
        String fecha = MensajeFormato.formatearFechaHora(mensaje.getCreatedAt());
        holder.fecha.setText(fecha);
        holder.fecha.setVisibility(fecha.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final LinearLayout burbuja;
        final TextView autor;
        final TextView cuerpo;
        final TextView fecha;

        Holder(@NonNull View itemView) {
            super(itemView);
            burbuja = itemView.findViewById(R.id.burbujaMensaje);
            autor = itemView.findViewById(R.id.textBurbujaAutor);
            cuerpo = itemView.findViewById(R.id.textBurbujaCuerpo);
            fecha = itemView.findViewById(R.id.textBurbujaFecha);
        }
    }
}
