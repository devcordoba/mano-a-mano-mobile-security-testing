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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {

    /** La Activity implementa esto para saber cuándo tocaron "Responder". */
    public interface OnResponderListener {
        void onResponder(Mensaje mensaje);

        void onAbrirHilo(Mensaje mensaje);
    }

    private final List<Mensaje> mensajes = new ArrayList<>();
    private final OnResponderListener listener;
    private final Map<Integer, String> titulos = new HashMap<>();
    private final Set<Integer> oportunidadesVoluntario = new HashSet<>();
    private final Set<Integer> oportunidadesOrganizacion = new HashSet<>();
    private final Map<Integer, Integer> propietarioOrganizacion = new HashMap<>();
    private boolean mostrandoRecibidos = true;

    public void setContexto(
            Map<Integer, String> titulos,
            Set<Integer> oportunidadesVoluntario,
            Set<Integer> oportunidadesOrganizacion,
            Map<Integer, Integer> propietarioOrganizacion
    ) {
        this.titulos.clear();
        this.oportunidadesVoluntario.clear();
        this.oportunidadesOrganizacion.clear();
        this.propietarioOrganizacion.clear();
        if (titulos != null) {
            this.titulos.putAll(titulos);
        }
        if (oportunidadesVoluntario != null) {
            this.oportunidadesVoluntario.addAll(oportunidadesVoluntario);
        }
        if (oportunidadesOrganizacion != null) {
            this.oportunidadesOrganizacion.addAll(oportunidadesOrganizacion);
        }
        if (propietarioOrganizacion != null) {
            this.propietarioOrganizacion.putAll(propietarioOrganizacion);
        }
        notifyDataSetChanged();
    }

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
        pintarRol(holder, mensaje);
        pintarOferta(holder, mensaje);
        String fecha = MensajeFormato.formatearFechaHora(mensaje.getCreatedAt());
        if (fecha.isEmpty()) {
            holder.textFecha.setVisibility(View.GONE);
        } else {
            holder.textFecha.setVisibility(View.VISIBLE);
            holder.textFecha.setText("Fecha: " + fecha);
        }
        holder.textCuerpo.setText(mensaje.getCuerpo());

        holder.itemView.setOnClickListener(v -> listener.onAbrirHilo(mensaje));
        holder.btnResponder.setVisibility(View.VISIBLE);
        holder.btnResponder.setText("Ver conversación");
        holder.btnResponder.setOnClickListener(v -> listener.onAbrirHilo(mensaje));
    }

    private void pintarRol(MensajeViewHolder holder, Mensaje mensaje) {
        int otro = mostrandoRecibidos ? mensaje.getRemitente() : mensaje.getDestinatario();
        boolean voluntario = oportunidadesVoluntario.contains(mensaje.getOportunidad());
        boolean organizacion = oportunidadesOrganizacion.contains(mensaje.getOportunidad());
        if (voluntario && organizacion) {
            Integer propietario = propietarioOrganizacion.get(mensaje.getOportunidad());
            voluntario = propietario != null && propietario == otro;
            organizacion = !voluntario;
        }

        String rol;
        int color;
        if (voluntario) {
            rol = mostrandoRecibidos
                    ? "Organización → vos, como voluntario"
                    : "Vos, como voluntario → organización";
            color = R.color.primary;
        } else if (organizacion) {
            rol = mostrandoRecibidos
                    ? "Voluntario → vos, como organización"
                    : "Vos, como organización → voluntario";
            color = R.color.info;
        } else {
            rol = mostrandoRecibidos ? "Recibido" : "Enviado";
            color = R.color.text_secondary;
        }
        holder.textRol.setText(rol);
        holder.textRol.setTextColor(holder.itemView.getContext().getColor(color));
    }

    private void pintarOferta(MensajeViewHolder holder, Mensaje mensaje) {
        String titulo = titulos.get(mensaje.getOportunidad());
        if (titulo == null || titulo.trim().isEmpty()) {
            holder.textOferta.setVisibility(View.GONE);
            return;
        }
        holder.textOferta.setVisibility(View.VISIBLE);
        holder.textOferta.setText("Oferta: " + titulo.trim());
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class MensajeViewHolder extends RecyclerView.ViewHolder {
        final TextView textContraparte;
        final TextView textRol;
        final TextView textOferta;
        final TextView textFecha;
        final TextView textCuerpo;
        final MaterialButton btnResponder;

        MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            textContraparte = itemView.findViewById(R.id.textContraparte);
            textRol = itemView.findViewById(R.id.textRol);
            textOferta = itemView.findViewById(R.id.textOferta);
            textFecha = itemView.findViewById(R.id.textFecha);
            textCuerpo = itemView.findViewById(R.id.textCuerpo);
            btnResponder = itemView.findViewById(R.id.btnResponder);
        }
    }
}