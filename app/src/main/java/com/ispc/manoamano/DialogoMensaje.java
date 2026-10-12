package com.ispc.manoamano;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ispc.manoamano.modelos.Mensaje;
import com.ispc.manoamano.modelos.MensajeRequest;
import com.ispc.manoamano.repositorios.MensajeRepository;
import com.ispc.manoamano.utilidades.ApiCallback;
import com.ispc.manoamano.utilidades.MensajeFormato;

public final class DialogoMensaje {

    private DialogoMensaje() {
    }

    public static void mostrar(AppCompatActivity activity, int destinatario, int oportunidadId) {
        if (destinatario < 1 || oportunidadId < 1) {
            Toast.makeText(activity, "No se puede abrir esta conversación.", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText edit = new EditText(activity);
        edit.setHint(R.string.mensajes_hint_respuesta);
        edit.setMinLines(3);
        int pad = (int) (12 * activity.getResources().getDisplayMetrics().density);
        edit.setPadding(pad, pad, pad, pad);

        AlertDialog dialogo = new MaterialAlertDialogBuilder(activity)
                .setTitle("Escribir")
                .setView(edit)
                .setNegativeButton(R.string.mensajes_cancelar, null)
                .setPositiveButton(R.string.mensajes_enviar, null)
                .create();

        dialogo.setOnShowListener(d -> {
            Button boton = dialogo.getButton(AlertDialog.BUTTON_POSITIVE);
            boton.setOnClickListener(v -> enviar(activity, dialogo, boton, edit, destinatario, oportunidadId));
        });
        dialogo.show();
    }

    private static void enviar(
            AppCompatActivity activity,
            AlertDialog dialogo,
            Button boton,
            EditText edit,
            int destinatario,
            int oportunidadId
    ) {
        String texto = edit.getText() == null ? "" : edit.getText().toString();
        int resultado = MensajeFormato.validarCuerpo(texto);
        if (resultado == MensajeFormato.CUERPO_VACIO) {
            Toast.makeText(activity, R.string.mensajes_error_vacio, Toast.LENGTH_SHORT).show();
            return;
        }
        if (resultado == MensajeFormato.CUERPO_DEMASIADO_LARGO) {
            Toast.makeText(activity, R.string.mensajes_error_largo, Toast.LENGTH_SHORT).show();
            return;
        }

        boton.setEnabled(false);
        new MensajeRepository(activity).enviar(
                new MensajeRequest(destinatario, oportunidadId, texto.trim()),
                new ApiCallback<Mensaje>(activity) {
                    @Override
                    protected void onSuccess(Mensaje body) {
                        if (activity.isFinishing()) {
                            return;
                        }
                        dialogo.dismiss();
                        Toast.makeText(activity, R.string.mensajes_enviado, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onError(int codigo) {
                        if (!activity.isFinishing()) {
                            boton.setEnabled(true);
                        }
                    }

                    @Override
                    protected void onNetworkError(Throwable t) {
                        if (!activity.isFinishing()) {
                            boton.setEnabled(true);
                        }
                    }
                }
        );
    }
}
