package com.ispc.manoamano;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ispc.manoamano.adaptadores.OportunidadAdapter;
import com.ispc.manoamano.modelos.Oportunidad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final int[] IMAGENES_OPORTUNIDADES = {
            R.drawable.example_img1,
            R.drawable.example_img2,
            R.drawable.example_img3,
            R.drawable.example_img4,
            R.drawable.example_img5
    };

    private final List<Oportunidad> todasLasOportunidades = new ArrayList<>();
    private final List<Oportunidad> oportunidadesVisibles = new ArrayList<>();
    private final Random random = new Random();
    private OportunidadAdapter adapter;
    private EditText editBuscar;
    private Spinner spinnerCausa;
    private Spinner spinnerActividad;
    private TextView textSinResultados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editBuscar = findViewById(R.id.editBuscar);
        spinnerCausa = findViewById(R.id.spinnerCausa);
        spinnerActividad = findViewById(R.id.spinnerActividad);
        textSinResultados = findViewById(R.id.textSinResultados);
        TextView tvBienvenida = findViewById(R.id.tvBienvenida);
        RecyclerView recyclerOportunidades = findViewById(R.id.recyclerOportunidades);

        String nombreUsuario = null;
        if (getIntent() != null && getIntent().hasExtra("EXTRA_USUARIO")) {
            nombreUsuario = getIntent().getStringExtra("EXTRA_USUARIO");
        }

        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            tvBienvenida.setText(R.string.bienvenida_visitante);
        } else {
            tvBienvenida.setText(getString(R.string.bienvenida_usuario, nombreUsuario));
        }

        cargarOportunidades();
        oportunidadesVisibles.addAll(todasLasOportunidades);
        adapter = new OportunidadAdapter(oportunidadesVisibles);
        recyclerOportunidades.setLayoutManager(new LinearLayoutManager(this));
        recyclerOportunidades.setAdapter(adapter);

        configurarSpinners();
        configurarBuscador();
        configurarNavegacion();
    }

    private void configurarSpinners() {
        List<String> causas = Arrays.asList(
                "Todas", "Animales", "Cultura y arte", "Deporte y recreación",
                "Derechos humanos", "Educación y alfabetización",
                "Emergencias y desastres", "Inclusión social", "Medio ambiente",
                "Otra", "Salud y bienestar");

        List<String> actividades = Arrays.asList(
                "Todas",
                "Accompañamiento a personas mayores",
                "Alfabetización y educación popular",
                "Apoyo escolar y tareas dirigidas",
                "Arte, cultura y talleres creativos",
                "Atención en merenderos y comedores comunitarios",
                "Campañas de concientización",
                "Cocina / logística",
                "Comunicación, diseño y redes sociales",
                "Cuidado de niños, niñas y adolescentes",
                "Defensa de derechos y acompañamiento legal básico",
                "Deporte, recreación y actividades al aire libre",
                "Emergencias y operativos solidarios",
                "Huertas urbanas y agricultura social",
                "Informática y alfabetización digital",
                "Interpretación y traducción",
                "Logística, depósito y distribución",
                "Mantenimiento, pintura y refacción",
                "Medio ambiente y limpieza de espacios públicos",
                "Música, teatro y eventos comunitarios",
                "Otra (detallar en descripción o requisitos)",
                "Recaudación de fondos y voluntariado administrativo",
                "Reforestación y trabajo al aire libre",
                "Salud comunitaria y primeros auxilios",
                "Trabajo al aire libre",
                "Visitas domiciliarias y contención social",
                "Voluntariado con animales y refugios",
                "Voluntariado en hospitales y centros de salud");

        ArrayAdapter<String> causasAdapter = new ArrayAdapter<>(
                this, R.layout.item_spinner, R.id.textSpinnerItem, causas);
        causasAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerCausa.setAdapter(causasAdapter);

        ArrayAdapter<String> actividadesAdapter = new ArrayAdapter<>(
                this, R.layout.item_spinner, R.id.textSpinnerItem, actividades);
        actividadesAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerActividad.setAdapter(actividadesAdapter);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltros();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hace falta realizar ninguna acción.
            }
        };
        spinnerCausa.setOnItemSelectedListener(listener);
        spinnerActividad.setOnItemSelectedListener(listener);
    }

    private void configurarBuscador() {
        editBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                aplicarFiltros();
            }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    private void aplicarFiltros() {
        String busqueda = editBuscar.getText().toString().trim().toLowerCase(Locale.ROOT);
        String causaElegida = spinnerCausa.getSelectedItem().toString();
        String actividadElegida = spinnerActividad.getSelectedItem().toString();

        oportunidadesVisibles.clear();
        for (Oportunidad oportunidad : todasLasOportunidades) {
            String textoCompleto = (oportunidad.getTitulo() + " "
                    + oportunidad.getUbicacion() + " "
                    + oportunidad.getOrganizacion() + " "
                    + oportunidad.getCausa() + " "
                    + oportunidad.getTipoActividad() + " "
                    + oportunidad.getResumen()).toLowerCase(Locale.ROOT);

            boolean coincideTexto = textoCompleto.contains(busqueda);
            boolean coincideCausa = causaElegida.equals("Todas")
                    || oportunidad.getCausa().equals(causaElegida);
            boolean coincideActividad = actividadElegida.equals("Todas")
                    || oportunidad.getTipoActividad().equals(actividadElegida);

            if (coincideTexto && coincideCausa && coincideActividad) {
                oportunidadesVisibles.add(oportunidad);
            }
        }

        adapter.notifyDataSetChanged();
        textSinResultados.setVisibility(
                oportunidadesVisibles.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void configurarNavegacion() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_inicio);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // Revisa que 'nav_contacto' coincida exactamente con el id del item en tu bottom_navigation_menu.xml
            if (id == R.id.nav_contacto) {
                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(intent);
                return true;
            }

            // Si tienes otros fragmentos o pantallas que cambian dentro de la misma actividad,
            // puedes agregar los "else if" para nav_inicio u otras opciones aquí.

            return true;
        });
    }

    private void cargarOportunidades() {
        todasLasOportunidades.add(new Oportunidad(10,
                "Festival barrial - equipo de producción", "Boedo, CABA", "Cultura en Barrio",
                "Cultura y arte", "Música, teatro y eventos comunitarios",
                "Viernes y sábado (fin de semana del evento)", 12, "2026-10-01", true,
                obtenerImagenAleatoria(),
                "Ayuda en montaje, acreditaciones y comunicación el día del evento."));
        todasLasOportunidades.add(new Oportunidad(9,
                "Taller de muralismo con jóvenes", "Barracas, CABA", "Cultura en Barrio",
                "Cultura y arte", "Arte, cultura y talleres creativos",
                "Sábados 14:00–18:00 por 4 semanas", 4, "2026-10-13", true,
                obtenerImagenAleatoria(),
                "Coordinación de diseño y pintura en muro de escuela secundaria. Materiales provistos."));
        todasLasOportunidades.add(new Oportunidad(8,
                "Paseo y socialización de perros en adopción", "Vicente López, GBA Norte", "Refugio Patitas",
                "Animales", "Voluntariado con animales y refugios",
                "Fines de semana, turnos flexibles", 10, "2026-10-03", true,
                obtenerImagenAleatoria(),
                "Salidas de 40 minutos por perro mediano. Inducción obligatoria el primer día."));
        todasLasOportunidades.add(new Oportunidad(7,
                "Comedor comunitario - turno de cocina y servicio", "La Boca, CABA", "Red Inclusión Buenos Aires",
                "Inclusión social", "Atención en merenderos y comedores comunitarios",
                "Domingos 11:00–14:00", 8, "2026-09-20", true,
                obtenerImagenAleatoria(),
                "Apoyo en preparación de viandas y servicio de mesa. Cocina industrial con protocolo de higiene."));
        todasLasOportunidades.add(new Oportunidad(6,
                "Mentoría para búsqueda de primer empleo", "San Telmo, CABA (híbrido)", "Red Inclusión Buenos Aires",
                "Inclusión social", "Campañas de concientización",
                "Viernes 15:00–18:00", 3, "2026-10-28", true,
                obtenerImagenAleatoria(),
                "Acompañamiento en armado de CV, simulacros de entrevista y uso de portales laborales."));
        todasLasOportunidades.add(new Oportunidad(5,
                "Acompañamiento a pacientes en sala de espera", "Saavedra, CABA", "Hospital Comunitario Norte",
                "Salud y bienestar", "Voluntariado en hospitales y centros de salud",
                "Lunes a viernes, turnos mañana o tarde", 5, "2026-09-23", true,
                obtenerImagenAleatoria(),
                "Orientación a familias, entrega de información y contención en guardia."));
        todasLasOportunidades.add(new Oportunidad(4,
                "Huerta comunitaria en Parque Patricios", "Parque Patricios, CABA", "Verde Urbano ONG",
                "Medio ambiente", "Huertas urbanas y agricultura social",
                "Miércoles 18:00–20:00", 6, "2026-10-11", true,
                obtenerImagenAleatoria(),
                "Mantenimiento semanal: riego, compost y siembra estacional. Ideal para quienes quieren aprender agricultura urbana."));
        todasLasOportunidades.add(new Oportunidad(3,
                "Jornada de limpieza del arroyo Maldonado", "Villa Crespo / Chacarita, CABA", "Verde Urbano ONG",
                "Medio ambiente", "Medio ambiente y limpieza de espacios públicos",
                "Sábado 9:00–13:00 (una jornada)", 15, "2026-09-27", true,
                obtenerImagenAleatoria(),
                "Operativo de limpieza y clasificación de residuos. Entrega de elementos de protección y refrigerio."));
        todasLasOportunidades.add(new Oportunidad(2,
                "Taller de alfabetización digital para adultos mayores", "Caballito, CABA", "Fundación Manos Unidas",
                "Educación y alfabetización", "Informática y alfabetización digital",
                "Sábados 10:00–12:00", 2, "2026-10-18", true,
                obtenerImagenAleatoria(),
                "Enseñanza básica de smartphone, correo y trámites en Mi Argentina. Grupos reducidos de hasta 8 personas."));
        todasLasOportunidades.add(new Oportunidad(1,
                "Apoyo escolar en merendero de Flores", "Flores, CABA", "Fundación Manos Unidas",
                "Educación y alfabetización", "Apoyo escolar y tareas dirigidas",
                "Martes y jueves 17:00–19:00", 4, "2026-10-04", true,
                obtenerImagenAleatoria(),
                "Buscamos voluntarios para acompañar a niños de primaria con tareas escolares y lectura. Se brinda capacitación inicial."));
    }

    private int obtenerImagenAleatoria() {
        return IMAGENES_OPORTUNIDADES[random.nextInt(IMAGENES_OPORTUNIDADES.length)];
    }
}
