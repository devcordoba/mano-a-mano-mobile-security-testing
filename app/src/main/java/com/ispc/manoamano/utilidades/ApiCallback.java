package com.ispc.manoamano.utilidades;

import android.content.Context;
import android.widget.Toast;

import com.ispc.manoamano.almacenamiento.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ApiCallback<T> implements Callback<T> {

    private final Context context;

    public ApiCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onResponse(
            Call<T> call,
            Response<T> response
    ) {

        if (response.isSuccessful()) {

            onSuccess(response.body());

            return;
        }

        String mensaje =
                HttpErrorHandler.obtenerMensaje(
                        response.code()
                );

        if (response.code() == 401) {
            new SessionManager(context).cerrarSesion();
        }

        Toast.makeText(
                context,
                mensaje,
                Toast.LENGTH_SHORT
        ).show();

        onError(response.code());
    }

    @Override
    public void onFailure(
            Call<T> call,
            Throwable t
    ) {

        if (call.isCanceled()) {
            return;
        }

        Toast.makeText(
                context,
                "No se pudo conectar con el servidor.",
                Toast.LENGTH_SHORT
        ).show();

        onNetworkError(t);
    }

    protected abstract void onSuccess(T body);

    protected void onError(int codigo) {
        // Si una Activity necesita hacer algo específico.
    }

    protected void onNetworkError(Throwable t) {
        // Si se necesita manejar un error de conexión.
    }
}
