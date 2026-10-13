package com.ispc.manoamano;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

public final class Volver {

    private Volver() {
    }

    public static void conectar(Activity activity) {
        View boton = activity.findViewById(R.id.btnVolver);
        if (boton != null) {
            boton.setOnClickListener(v -> {
                if (activity.isTaskRoot()) {
                    activity.startActivity(new Intent(activity, MainActivity.class));
                }
                activity.finish();
            });
        }
    }
}
