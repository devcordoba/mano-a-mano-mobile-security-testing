package com.ispc.manoamano;

import java.util.Arrays;
import java.util.List;

public final class AyudaContenido {

    public static final class Seccion {
        public final int titulo;
        public final int cuerpo;

        public Seccion(int titulo, int cuerpo) {
            this.titulo = titulo;
            this.cuerpo = cuerpo;
        }
    }

    private AyudaContenido() {
    }

    public static List<Seccion> secciones() {
        return Arrays.asList(
                new Seccion(R.string.ayuda_quienes_titulo, R.string.ayuda_quienes_cuerpo),
                new Seccion(R.string.ayuda_mision_titulo, R.string.ayuda_mision_cuerpo),
                new Seccion(R.string.ayuda_proposito_titulo, R.string.ayuda_proposito_cuerpo),
                new Seccion(R.string.ayuda_porque_titulo, R.string.ayuda_porque_cuerpo)
        );
    }
}
