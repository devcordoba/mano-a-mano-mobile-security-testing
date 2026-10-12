package com.ispc.manoamano;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ispc.manoamano.almacenamiento.SessionManager;
import com.ispc.manoamano.modelos.Postulacion;
import com.ispc.manoamano.repositorios.PostulacionRepository;
import com.ispc.manoamano.utilidades.ApiCallback;
import com.ispc.manoamano.utilidades.MensajeFormato;

import java.util.List;

public class PanelUsuario {

    private final AppCompatActivity activity;
    private final PostulacionRepository postulaciones;
    private final SessionManager session;
    private final LinearLayout lista;
    private final TextView vacio;
    private final View rueda;

    public PanelUsuario(AppCompatActivity activity) {
        this.activity = activity;
        this.postulaciones = new PostulacionRepository(activity);
        this.session = new SessionManager(activity);
        this.lista = activity.findViewById(R.id.listaPostulaciones);
        this.vacio = activity.findViewById(R.id.textPostulacionesVacio);
        this.rueda = activity.findViewById(R.id.rueditaPostulaciones);
    }

    public void cargar() {
        rueda.setVisibility(View.VISIBLE);
        postulaciones.obtenerPorVoluntario(
                session.obtenerUsuarioId(),
                new ApiCallback<List<Postulacion>>(activity) {
                    @Override
                    protected void onSuccess(List<Postulacion> body) {
                        rueda.setVisibility(View.GONE);
                        if (activity.isFinishing()) {
                            return;
                        }
                        pintar(body);
                    }

                    @Override
                    protected void onError(int codigo) {
                        rueda.setVisibility(View.GONE);
                    }

                    @Override
                    protected void onNetworkError(Throwable t) {
                        rueda.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void pintar(List<Postulacion> items) {
        lista.removeAllViews();
        boolean hay = items != null && !items.isEmpty();
        vacio.setVisibility(hay ? View.GONE : View.VISIBLE);
        if (!hay) {
            return;
        }
        for (Postulacion item : items) {
            View fila = activity.getLayoutInflater()
                    .inflate(R.layout.item_postulacion_simple, lista, false);
            ((TextView) fila.findViewById(R.id.tvTituloItem)).setText(texto(item.getOportunidadTitulo()));
            TextView estadoView = fila.findViewById(R.id.tvEstadoItem);
            estadoView.setText(nombreEstado(item.getEstado()));
            estadoView.setTextColor(activity.getColor(colorEstado(item.getEstado())));
            String fecha = MensajeFormato.formatearFecha(item.getCreatedAt());
            TextView detalle = fila.findViewById(R.id.tvDetalleItem);
            if (fecha.isEmpty()) {
                detalle.setVisibility(View.GONE);
            } else {
                detalle.setText(fecha);
            }
            fila.findViewById(R.id.btnEscribirItem).setOnClickListener(v ->
                    abrirConversacion(
                            item.getOrganizacionPropietario(),
                            item.getOportunidad(),
                            texto(item.getOportunidadTitulo())
                    )
            );
            lista.addView(fila);
        }
    }

    private static String nombreEstado(String codigo) {
        if ("ACE".equals(codigo)) {
            return "Aceptada";
        }
        if ("REC".equals(codigo)) {
            return "Rechazada";
        }
        if ("CAN".equals(codigo)) {
            return "Cancelada";
        }
        return "Pendiente";
    }

    private static int colorEstado(String codigo) {
        if ("ACE".equals(codigo)) {
            return R.color.success;
        }
        if ("REC".equals(codigo)) {
            return R.color.danger;
        }
        if ("CAN".equals(codigo)) {
            return R.color.text_secondary;
        }
        return R.color.warning;
    }

    private void abrirConversacion(int otroId, int oportunidadId, String titulo) {
        Intent intent = new Intent(activity, MensajesActivity.class);
        intent.putExtra(MensajesActivity.EXTRA_OTRO_ID, otroId);
        intent.putExtra(MensajesActivity.EXTRA_OPORTUNIDAD_ID, oportunidadId);
        intent.putExtra(MensajesActivity.EXTRA_TITULO, titulo);
        activity.startActivity(intent);
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor;
    }
}
