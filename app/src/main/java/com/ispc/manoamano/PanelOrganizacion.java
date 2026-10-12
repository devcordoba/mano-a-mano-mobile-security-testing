package com.ispc.manoamano;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.bumptech.glide.Glide;
import com.ispc.manoamano.BuildConfig;
import com.ispc.manoamano.almacenamiento.SessionManager;
import com.ispc.manoamano.modelos.Causa;
import com.ispc.manoamano.modelos.OportunidadGestion;
import com.ispc.manoamano.modelos.OportunidadRequest;
import com.ispc.manoamano.modelos.Organizacion;
import com.ispc.manoamano.modelos.OrganizacionRequest;
import com.ispc.manoamano.modelos.Postulacion;
import com.ispc.manoamano.modelos.PostulacionBloque;
import com.ispc.manoamano.modelos.PostulacionRequest;
import com.ispc.manoamano.modelos.TipoActividad;
import com.ispc.manoamano.repositorios.CausaRepository;
import com.ispc.manoamano.repositorios.OportunidadRepository;
import com.ispc.manoamano.repositorios.OrganizacionRepository;
import com.ispc.manoamano.repositorios.PostulacionRepository;
import com.ispc.manoamano.repositorios.TipoActividadRepository;
import com.ispc.manoamano.utilidades.ApiCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PanelOrganizacion {

    private static final int SEC_DATOS = 0;
    private static final int SEC_PUBLICAR = 1;
    private static final int SEC_OFERTAS = 2;
    private static final int SEC_POSTULANTES = 3;

    private static final String[] ESTADOS = {"PEN", "ACE", "REC"};
    private static final String[] ESTADOS_NOMBRE = {"Pendiente", "Aceptada", "Rechazada"};

    private final AppCompatActivity activity;
    private final SessionManager session;
    private final OrganizacionRepository organizaciones;
    private final OportunidadRepository oportunidades;
    private final PostulacionRepository postulaciones;
    private final List<Causa> causas = new ArrayList<>();
    private final List<TipoActividad> tipos = new ArrayList<>();

    private final EditText editNombre;
    private final EditText editEmail;
    private final EditText editDescripcion;
    private final EditText editTelefono;
    private final EditText editSitio;
    private final TextView btnEliminarOrg;
    private final EditText editTitulo;
    private final EditText editPubDescripcion;
    private final EditText editUbicacion;
    private final EditText editDisponibilidad;
    private final EditText editCupos;
    private final Spinner spinnerCausa;
    private final Spinner spinnerTipo;
    private final TextView btnPublicar;
    private final TextView btnCancelarPublicacion;
    private final LinearLayout listaPublicaciones;
    private final LinearLayout listaPostulantes;
    private final TextView vacioPublicaciones;
    private final TextView vacioPostulantes;
    private final TextView tabDatos;
    private final TextView tabPublicar;
    private final TextView tabOfertas;
    private final TextView tabPostulantes;
    private final View seccionDatos;
    private final View seccionPublicar;
    private final View seccionOfertas;
    private final View seccionPostulantes;
    private final View rueda;
    private int cargasActivas;
    private boolean accionEnCurso;
    private final ImageView imgPubPreview;
    private final TextView textPubImagen;
    private final TextView btnQuitarImagen;
    private final ActivityResultLauncher<String> elegirImagen;

    private Organizacion organizacion;
    private int editandoId = -1;
    private boolean editandoActiva = true;
    private boolean editandoTieneImagen;
    private String editandoVersion;
    private File imagenArchivo;
    private String imagenTipo;
    private int causaPendiente = -1;
    private int tipoPendiente = -1;

    public PanelOrganizacion(AppCompatActivity activity) {
        this.activity = activity;
        this.session = new SessionManager(activity);
        this.organizaciones = new OrganizacionRepository(activity);
        this.oportunidades = new OportunidadRepository(activity);
        this.postulaciones = new PostulacionRepository(activity);

        editNombre = activity.findViewById(R.id.editOrgNombre);
        editEmail = activity.findViewById(R.id.editOrgEmail);
        editDescripcion = activity.findViewById(R.id.editOrgDescripcion);
        editTelefono = activity.findViewById(R.id.editOrgTelefono);
        editSitio = activity.findViewById(R.id.editOrgSitio);
        btnEliminarOrg = activity.findViewById(R.id.btnEliminarOrg);
        editTitulo = activity.findViewById(R.id.editPubTitulo);
        editPubDescripcion = activity.findViewById(R.id.editPubDescripcion);
        editUbicacion = activity.findViewById(R.id.editPubUbicacion);
        editDisponibilidad = activity.findViewById(R.id.editPubDisponibilidad);
        editCupos = activity.findViewById(R.id.editPubCupos);
        spinnerCausa = activity.findViewById(R.id.spinnerPubCausa);
        spinnerTipo = activity.findViewById(R.id.spinnerPubTipo);
        btnPublicar = activity.findViewById(R.id.btnPublicar);
        btnCancelarPublicacion = activity.findViewById(R.id.btnCancelarPublicacion);
        listaPublicaciones = activity.findViewById(R.id.listaPublicaciones);
        listaPostulantes = activity.findViewById(R.id.listaPostulantes);
        vacioPublicaciones = activity.findViewById(R.id.textPublicacionesVacio);
        vacioPostulantes = activity.findViewById(R.id.textPostulantesVacio);
        tabDatos = activity.findViewById(R.id.tabOrgDatos);
        tabPublicar = activity.findViewById(R.id.tabOrgPublicar);
        tabOfertas = activity.findViewById(R.id.tabOrgOfertas);
        tabPostulantes = activity.findViewById(R.id.tabOrgPostulantes);
        seccionDatos = activity.findViewById(R.id.seccionOrgDatos);
        seccionPublicar = activity.findViewById(R.id.seccionOrgPublicar);
        seccionOfertas = activity.findViewById(R.id.seccionOrgOfertas);
        seccionPostulantes = activity.findViewById(R.id.seccionOrgPostulantes);
        rueda = activity.findViewById(R.id.rueditaOrganizacion);
        imgPubPreview = activity.findViewById(R.id.imgPubPreview);
        textPubImagen = activity.findViewById(R.id.textPubImagen);
        btnQuitarImagen = activity.findViewById(R.id.btnQuitarImagen);
        elegirImagen = activity.registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                this::usarImagen
        );

        activity.findViewById(R.id.btnGuardarOrg).setOnClickListener(v -> guardarOrganizacion());
        activity.findViewById(R.id.btnElegirImagen).setOnClickListener(v -> elegirImagen.launch("image/*"));
        btnQuitarImagen.setOnClickListener(v -> quitarImagenElegida());
        btnEliminarOrg.setOnClickListener(v -> confirmarEliminarOrganizacion());
        btnPublicar.setOnClickListener(v -> guardarPublicacion());
        btnCancelarPublicacion.setOnClickListener(v -> limpiarPublicacion());
        tabDatos.setOnClickListener(v -> mostrarSeccion(SEC_DATOS));
        tabPublicar.setOnClickListener(v -> mostrarSeccion(SEC_PUBLICAR));
        tabOfertas.setOnClickListener(v -> mostrarSeccion(SEC_OFERTAS));
        tabPostulantes.setOnClickListener(v -> mostrarSeccion(SEC_POSTULANTES));
        mostrarSeccion(SEC_DATOS);
    }

    private void mostrarSeccion(int seccion) {
        seccionDatos.setVisibility(seccion == SEC_DATOS ? View.VISIBLE : View.GONE);
        seccionPublicar.setVisibility(seccion == SEC_PUBLICAR ? View.VISIBLE : View.GONE);
        seccionOfertas.setVisibility(seccion == SEC_OFERTAS ? View.VISIBLE : View.GONE);
        seccionPostulantes.setVisibility(seccion == SEC_POSTULANTES ? View.VISIBLE : View.GONE);
        pintarTab(tabDatos, seccion == SEC_DATOS);
        pintarTab(tabPublicar, seccion == SEC_PUBLICAR);
        pintarTab(tabOfertas, seccion == SEC_OFERTAS);
        pintarTab(tabPostulantes, seccion == SEC_POSTULANTES);
    }

    private void pintarTab(TextView tab, boolean activo) {
        tab.setBackgroundResource(activo ? R.drawable.boton_primario : R.drawable.button_login);
        tab.setTextColor(activity.getColor(activo ? R.color.white : R.color.primary_dark));
    }

    public void cargar() {
        cargasActivas = 3;
        rueda.setVisibility(View.VISIBLE);
        cargarCatalogos();
        cargarOrganizacion();
        cargarPublicaciones();
        cargarPostulantes();
    }

    private void terminoCarga() {
        if (cargasActivas <= 0) {
            return;
        }
        cargasActivas--;
        if (cargasActivas == 0 && !accionEnCurso) {
            rueda.setVisibility(View.GONE);
        }
    }

    private void mostrarAccion(boolean activa) {
        accionEnCurso = activa;
        if (activa) {
            rueda.bringToFront();
            rueda.setVisibility(View.VISIBLE);
            return;
        }
        if (cargasActivas == 0) {
            rueda.setVisibility(View.GONE);
        }
    }

    private void cargarCatalogos() {
        if (causas.isEmpty()) {
            new CausaRepository(activity).obtenerCausas(new ApiCallback<List<Causa>>(activity) {
                @Override
                protected void onSuccess(List<Causa> body) {
                    if (activity.isFinishing() || body == null) {
                        return;
                    }
                    causas.clear();
                    causas.addAll(body);
                    llenarSpinner(spinnerCausa, nombresCausas());
                    seleccionar(spinnerCausa, causas, causaPendiente);
                }
            });
        }
        if (tipos.isEmpty()) {
            new TipoActividadRepository(activity).obtenerTiposActividad(
                    new ApiCallback<List<TipoActividad>>(activity) {
                        @Override
                        protected void onSuccess(List<TipoActividad> body) {
                            if (activity.isFinishing() || body == null) {
                                return;
                            }
                            tipos.clear();
                            tipos.addAll(body);
                            llenarSpinner(spinnerTipo, nombresTipos());
                            seleccionar(spinnerTipo, tipos, tipoPendiente);
                        }
                    }
            );
        }
    }

    private void cargarOrganizacion() {
        organizaciones.obtenerPorPropietario(
                session.obtenerUsuarioId(),
                new ApiCallback<List<Organizacion>>(activity) {
                    @Override
                    protected void onSuccess(List<Organizacion> body) {
                        terminoCarga();
                        if (activity.isFinishing()) {
                            return;
                        }
                        organizacion = body == null || body.isEmpty() ? null : body.get(0);
                        mostrarOrganizacion();
                    }

                    @Override
                    protected void onError(int codigo) {
                        terminoCarga();
                    }

                    @Override
                    protected void onNetworkError(Throwable t) {
                        terminoCarga();
                    }
                }
        );
    }

    private void mostrarOrganizacion() {
        if (organizacion == null) {
            editNombre.setText("");
            editEmail.setText("");
            editDescripcion.setText("");
            editTelefono.setText("");
            editSitio.setText("");
            btnEliminarOrg.setVisibility(View.GONE);
            return;
        }
        editNombre.setText(texto(organizacion.getNombrePublico()));
        editEmail.setText(texto(organizacion.getEmailContacto()));
        editDescripcion.setText(texto(organizacion.getDescripcion()));
        editTelefono.setText(texto(organizacion.getTelefono()));
        editSitio.setText(texto(organizacion.getSitioWeb()));
        btnEliminarOrg.setVisibility(View.VISIBLE);
    }

    private void guardarOrganizacion() {
        String nombre = valor(editNombre);
        String email = valor(editEmail);
        if (nombre.isEmpty() || email.isEmpty()) {
            Toast.makeText(activity, "Nombre y email son obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }
        String sitio = valor(editSitio);
        if (!sitio.isEmpty() && !sitio.startsWith("http://") && !sitio.startsWith("https://")) {
            sitio = "https://" + sitio;
        }
        OrganizacionRequest request = new OrganizacionRequest(
                nombre,
                valor(editDescripcion),
                email,
                valor(editTelefono),
                sitio
        );
        mostrarAccion(true);
        ApiCallback<Organizacion> callback = new ApiCallback<Organizacion>(activity) {
            @Override
            protected void onSuccess(Organizacion body) {
                mostrarAccion(false);
                if (activity.isFinishing()) {
                    return;
                }
                organizacion = body;
                mostrarOrganizacion();
                Toast.makeText(activity, "Organización guardada.", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onError(int codigo) {
                mostrarAccion(false);
            }

            @Override
            protected void onNetworkError(Throwable t) {
                mostrarAccion(false);
            }
        };
        if (organizacion == null) {
            organizaciones.crear(request, callback);
        } else {
            organizaciones.editar(organizacion.getId(), request, callback);
        }
    }

    private void confirmarEliminarOrganizacion() {
        if (organizacion == null) {
            return;
        }
        new MaterialAlertDialogBuilder(activity)
                .setTitle("Eliminar organización")
                .setMessage("Se va a borrar tu organización y sus ofertas. Esta acción no se puede deshacer.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (d, w) -> {
                    mostrarAccion(true);
                    organizaciones.eliminar(organizacion.getId(), new ApiCallback<Void>(activity) {
                        @Override
                        protected void onSuccess(Void body) {
                            mostrarAccion(false);
                            if (activity.isFinishing()) {
                                return;
                            }
                            organizacion = null;
                            mostrarOrganizacion();
                            cargarPublicaciones();
                            cargarPostulantes();
                            Toast.makeText(activity, "Organización eliminada.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        protected void onError(int codigo) {
                            mostrarAccion(false);
                        }

                        @Override
                        protected void onNetworkError(Throwable t) {
                            mostrarAccion(false);
                        }
                    });
                })
                .show();
    }

    private void guardarPublicacion() {
        if (organizacion == null) {
            Toast.makeText(activity, "Primero guardá tu organización.", Toast.LENGTH_SHORT).show();
            return;
        }
        String titulo = valor(editTitulo);
        String descripcion = valor(editPubDescripcion);
        String ubicacion = valor(editUbicacion);
        String disponibilidad = valor(editDisponibilidad);
        if (titulo.isEmpty() || descripcion.isEmpty() || ubicacion.isEmpty() || disponibilidad.isEmpty()) {
            Toast.makeText(activity, "Completá título, descripción, ubicación y disponibilidad.", Toast.LENGTH_SHORT).show();
            return;
        }
        int cupos;
        try {
            cupos = Integer.parseInt(valor(editCupos));
        } catch (NumberFormatException e) {
            Toast.makeText(activity, "Los cupos tienen que ser un número.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cupos < 1) {
            Toast.makeText(activity, "Tiene que haber al menos 1 cupo.", Toast.LENGTH_SHORT).show();
            return;
        }
        int posCausa = spinnerCausa.getSelectedItemPosition();
        int posTipo = spinnerTipo.getSelectedItemPosition();
        if (posCausa < 0 || posCausa >= causas.size() || posTipo < 0 || posTipo >= tipos.size()) {
            Toast.makeText(activity, "Elegí causa y tipo de actividad.", Toast.LENGTH_SHORT).show();
            return;
        }
        OportunidadRequest request = new OportunidadRequest(
                organizacion.getId(),
                titulo,
                descripcion,
                ubicacion,
                causas.get(posCausa).getId(),
                tipos.get(posTipo).getId(),
                disponibilidad,
                "",
                cupos,
                null,
                editandoActiva
        );
        mostrarAccion(true);
        ApiCallback<OportunidadGestion> callback = new ApiCallback<OportunidadGestion>(activity) {
            @Override
            protected void onSuccess(OportunidadGestion body) {
                mostrarAccion(false);
                if (activity.isFinishing()) {
                    return;
                }
                limpiarPublicacion();
                cargarPublicaciones();
                mostrarSeccion(SEC_OFERTAS);
                Toast.makeText(activity, "Oferta guardada.", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onError(int codigo) {
                mostrarAccion(false);
            }

            @Override
            protected void onNetworkError(Throwable t) {
                mostrarAccion(false);
            }
        };
        if (imagenArchivo != null) {
            if (editandoId < 1) {
                oportunidades.crearConImagen(request, imagenArchivo, imagenTipo, callback);
            } else {
                oportunidades.editarConImagen(editandoId, request, imagenArchivo, imagenTipo, callback);
            }
        } else if (editandoId < 1) {
            oportunidades.crear(request, callback);
        } else {
            oportunidades.editar(editandoId, request, callback);
        }
    }

    private void usarImagen(Uri uri) {
        if (uri == null) {
            return;
        }
        String tipo = activity.getContentResolver().getType(uri);
        String extension;
        String contentType;
        if ("image/png".equals(tipo)) {
            extension = "png";
            contentType = "image/png";
        } else if ("image/jpeg".equals(tipo) || "image/jpg".equals(tipo)) {
            extension = "jpg";
            contentType = "image/jpeg";
        } else {
            Toast.makeText(activity, "Solo se aceptan JPG o PNG.", Toast.LENGTH_SHORT).show();
            return;
        }
        File destino = new File(activity.getCacheDir(), "oferta-" + System.currentTimeMillis() + "." + extension);
        try (InputStream entrada = activity.getContentResolver().openInputStream(uri);
             FileOutputStream salida = new FileOutputStream(destino)) {
            if (entrada == null) {
                destino.delete();
                Toast.makeText(activity, "No se pudo leer la imagen.", Toast.LENGTH_SHORT).show();
                return;
            }
            byte[] buffer = new byte[8192];
            int leidos;
            long total = 0;
            while ((leidos = entrada.read(buffer)) != -1) {
                total += leidos;
                if (total > 5L * 1024 * 1024) {
                    destino.delete();
                    Toast.makeText(activity, "La imagen no puede superar 5 MB.", Toast.LENGTH_SHORT).show();
                    return;
                }
                salida.write(buffer, 0, leidos);
            }
        } catch (IOException e) {
            destino.delete();
            Toast.makeText(activity, "No se pudo leer la imagen.", Toast.LENGTH_SHORT).show();
            return;
        }
        borrarImagenTemporal();
        imagenArchivo = destino;
        imagenTipo = contentType;
        Glide.with(activity).load(destino).into(imgPubPreview);
        imgPubPreview.setVisibility(View.VISIBLE);
        textPubImagen.setText("Imagen elegida. Se va a usar al guardar.");
        btnQuitarImagen.setVisibility(View.VISIBLE);
    }

    private void quitarImagenElegida() {
        borrarImagenTemporal();
        imagenArchivo = null;
        imagenTipo = null;
        if (editandoTieneImagen && editandoId > 0) {
            mostrarImagenActual(editandoId);
            return;
        }
        Glide.with(activity).clear(imgPubPreview);
        imgPubPreview.setVisibility(View.GONE);
        textPubImagen.setText("Sin imagen. Si no elegís una, se genera sola.");
        btnQuitarImagen.setVisibility(View.GONE);
    }

    private void mostrarImagenActual(int id) {
        String base = BuildConfig.MANO_API_BASE_URL;
        if (!base.endsWith("/")) {
            base = base + "/";
        }
        String url = base + "api/oportunidades/" + id + "/imagen/";
        if (editandoVersion != null && !editandoVersion.isEmpty()) {
            url = url + "?v=" + Uri.encode(editandoVersion);
        }
        Glide.with(activity)
                .load(url)
                .into(imgPubPreview);
        imgPubPreview.setVisibility(View.VISIBLE);
        textPubImagen.setText("Imagen actual. Elegí otra para cambiarla.");
        btnQuitarImagen.setVisibility(View.GONE);
    }

    private void borrarImagenTemporal() {
        if (imagenArchivo != null && imagenArchivo.exists()) {
            imagenArchivo.delete();
        }
    }

    private void limpiarPublicacion() {
        editandoId = -1;
        editandoActiva = true;
        causaPendiente = -1;
        tipoPendiente = -1;
        editTitulo.setText("");
        editPubDescripcion.setText("");
        editUbicacion.setText("");
        editDisponibilidad.setText("");
        editCupos.setText("");
        btnPublicar.setText("Publicar oferta");
        btnCancelarPublicacion.setVisibility(View.GONE);
        editandoTieneImagen = false;
        editandoVersion = null;
        quitarImagenElegida();
    }

    private void cargarPublicaciones() {
        oportunidades.obtenerPorPropietario(
                session.obtenerUsuarioId(),
                null,
                new ApiCallback<List<OportunidadGestion>>(activity) {
                    @Override
                    protected void onSuccess(List<OportunidadGestion> body) {
                        terminoCarga();
                        if (activity.isFinishing()) {
                            return;
                        }
                        pintarPublicaciones(body);
                    }

                    @Override
                    protected void onError(int codigo) {
                        terminoCarga();
                    }

                    @Override
                    protected void onNetworkError(Throwable t) {
                        terminoCarga();
                    }
                }
        );
    }

    private void pintarPublicaciones(List<OportunidadGestion> items) {
        listaPublicaciones.removeAllViews();
        boolean hay = items != null && !items.isEmpty();
        vacioPublicaciones.setVisibility(hay ? View.GONE : View.VISIBLE);
        if (!hay) {
            return;
        }
        for (OportunidadGestion item : items) {
            View fila = activity.getLayoutInflater()
                    .inflate(R.layout.item_publicacion_simple, listaPublicaciones, false);
            String estado = item.isActiva() ? "Activa" : "Pausada";
            ((TextView) fila.findViewById(R.id.tvTituloItem)).setText(texto(item.getTitulo()) + " · " + estado);
            TextView pausar = fila.findViewById(R.id.btnPausarItem);
            pausar.setText(item.isActiva() ? "Pausar" : "Activar");
            pausar.setOnClickListener(v -> cambiarActiva(item));
            fila.findViewById(R.id.btnEditarItem).setOnClickListener(v -> editar(item));
            fila.findViewById(R.id.btnEliminarItem).setOnClickListener(v -> confirmarEliminarPublicacion(item.getId()));
            listaPublicaciones.addView(fila);
        }
    }

    private void editar(OportunidadGestion item) {
        editandoId = item.getId();
        editandoActiva = item.isActiva();
        editTitulo.setText(texto(item.getTitulo()));
        editPubDescripcion.setText(texto(item.getDescripcion()));
        editUbicacion.setText(texto(item.getUbicacion()));
        editDisponibilidad.setText(texto(item.getDisponibilidad()));
        editCupos.setText(String.valueOf(item.getCupos()));
        causaPendiente = item.getCausa();
        tipoPendiente = item.getTipoActividad();
        seleccionar(spinnerCausa, causas, causaPendiente);
        seleccionar(spinnerTipo, tipos, tipoPendiente);
        btnPublicar.setText("Guardar cambios");
        btnCancelarPublicacion.setVisibility(View.VISIBLE);
        editandoTieneImagen = item.isTieneImagen();
        editandoVersion = item.getUpdatedAt();
        borrarImagenTemporal();
        imagenArchivo = null;
        imagenTipo = null;
        if (editandoTieneImagen) {
            mostrarImagenActual(item.getId());
        } else {
            Glide.with(activity).clear(imgPubPreview);
            imgPubPreview.setVisibility(View.GONE);
            textPubImagen.setText("Sin imagen. Si no elegís una, se genera sola.");
            btnQuitarImagen.setVisibility(View.GONE);
        }
        mostrarSeccion(SEC_PUBLICAR);
    }

    private void cambiarActiva(OportunidadGestion item) {
        String fecha = item.getFechaActividad();
        if (fecha != null && fecha.length() >= 10) {
            fecha = fecha.substring(0, 10);
        } else {
            fecha = null;
        }
        OportunidadRequest request = new OportunidadRequest(
                item.getOrganizacion(),
                texto(item.getTitulo()),
                texto(item.getDescripcion()),
                texto(item.getUbicacion()),
                item.getCausa(),
                item.getTipoActividad(),
                texto(item.getDisponibilidad()),
                texto(item.getRequisitos()),
                item.getCupos() < 1 ? 1 : item.getCupos(),
                fecha,
                !item.isActiva()
        );
        mostrarAccion(true);
        oportunidades.editar(item.getId(), request, new ApiCallback<OportunidadGestion>(activity) {
            @Override
            protected void onSuccess(OportunidadGestion body) {
                mostrarAccion(false);
                if (activity.isFinishing()) {
                    return;
                }
                Toast.makeText(
                        activity,
                        item.isActiva() ? "Oferta pausada." : "Oferta activa.",
                        Toast.LENGTH_SHORT
                ).show();
                cargarPublicaciones();
            }

            @Override
            protected void onError(int codigo) {
                mostrarAccion(false);
            }

            @Override
            protected void onNetworkError(Throwable t) {
                mostrarAccion(false);
            }
        });
    }

    private void confirmarEliminarPublicacion(int id) {
        new AlertDialog.Builder(activity)
                .setMessage("¿Eliminar esta publicación?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (d, w) -> {
                    mostrarAccion(true);
                    oportunidades.eliminar(id, new ApiCallback<Void>(activity) {
                        @Override
                        protected void onSuccess(Void body) {
                            mostrarAccion(false);
                            if (activity.isFinishing()) {
                                return;
                            }
                            if (editandoId == id) {
                                limpiarPublicacion();
                            }
                            cargarPublicaciones();
                            cargarPostulantes();
                        }

                        @Override
                        protected void onError(int codigo) {
                            mostrarAccion(false);
                        }

                        @Override
                        protected void onNetworkError(Throwable t) {
                            mostrarAccion(false);
                        }
                    });
                })
                .show();
    }

    private void cargarPostulantes() {
        postulaciones.obtenerBloquesOrganizador(new ApiCallback<List<PostulacionBloque>>(activity) {
            @Override
            protected void onSuccess(List<PostulacionBloque> body) {
                terminoCarga();
                if (!activity.isFinishing()) {
                    pintarPostulantes(body);
                }
            }

            @Override
            protected void onError(int codigo) {
                terminoCarga();
            }

            @Override
            protected void onNetworkError(Throwable t) {
                terminoCarga();
            }
        });
    }

    private void pintarPostulantes(List<PostulacionBloque> bloques) {
        listaPostulantes.removeAllViews();
        boolean hay = false;
        if (bloques != null) {
            for (PostulacionBloque bloque : bloques) {
                if (bloque.getItems() == null || bloque.getItems().isEmpty()) {
                    continue;
                }
                hay = true;
                TextView titulo = new TextView(activity);
                titulo.setText(texto(bloque.getTitulo()));
                titulo.setTextColor(activity.getColor(R.color.text_primary));
                titulo.setTextSize(18);
                titulo.setTypeface(titulo.getTypeface(), android.graphics.Typeface.BOLD);
                titulo.setPadding(0, 16, 0, 8);
                listaPostulantes.addView(titulo);
                for (Postulacion item : bloque.getItems()) {
                    listaPostulantes.addView(filaPostulante(item, bloque.getOportunidadId()));
                }
            }
        }
        vacioPostulantes.setVisibility(hay ? View.GONE : View.VISIBLE);
    }

    private View filaPostulante(Postulacion item, int oportunidadId) {
        View fila = activity.getLayoutInflater()
                .inflate(R.layout.item_postulante_simple, listaPostulantes, false);
        ((TextView) fila.findViewById(R.id.tvNombreItem)).setText(texto(item.getVoluntarioUsername()));
        TextView fijo = fila.findViewById(R.id.tvEstadoFijoItem);
        Spinner spinner = fila.findViewById(R.id.spinnerEstadoItem);
        View guardar = fila.findViewById(R.id.btnGuardarEstadoItem);
        if ("CAN".equals(item.getEstado())) {
            fijo.setText("Cancelada");
            fijo.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.GONE);
            guardar.setVisibility(View.GONE);
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    activity,
                    R.layout.item_spinner,
                    R.id.textSpinnerItem,
                    ESTADOS_NOMBRE
            );
            adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
            spinner.setAdapter(adapter);
            spinner.setSelection(indiceEstado(item.getEstado()));
            guardar.setOnClickListener(v -> {
                int pos = spinner.getSelectedItemPosition();
                if (pos < 0 || pos >= ESTADOS.length) {
                    return;
                }
                mostrarAccion(true);
                postulaciones.editarEstado(
                        item.getId(),
                        new PostulacionRequest(ESTADOS[pos]),
                        new ApiCallback<Postulacion>(activity) {
                            @Override
                            protected void onSuccess(Postulacion body) {
                                mostrarAccion(false);
                                if (!activity.isFinishing()) {
                                    Toast.makeText(activity, "Estado actualizado.", Toast.LENGTH_SHORT).show();
                                    cargarPostulantes();
                                }
                            }

                            @Override
                            protected void onError(int codigo) {
                                mostrarAccion(false);
                            }

                            @Override
                            protected void onNetworkError(Throwable t) {
                                mostrarAccion(false);
                            }
                        }
                );
            });
        }
        fila.findViewById(R.id.btnEscribirItem).setOnClickListener(v -> {
            Intent intent = new Intent(activity, MensajesActivity.class);
            intent.putExtra(MensajesActivity.EXTRA_OTRO_ID, item.getVoluntario());
            intent.putExtra(MensajesActivity.EXTRA_OPORTUNIDAD_ID, oportunidadId);
            intent.putExtra(MensajesActivity.EXTRA_TITULO, texto(item.getVoluntarioUsername()));
            activity.startActivity(intent);
        });
        return fila;
    }

    private void llenarSpinner(Spinner spinner, List<String> nombres) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                activity,
                R.layout.item_spinner,
                R.id.textSpinnerItem,
                nombres
        );
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner.setAdapter(adapter);
    }

    private List<String> nombresCausas() {
        List<String> nombres = new ArrayList<>();
        for (Causa causa : causas) {
            nombres.add(causa.getNombre());
        }
        return nombres;
    }

    private List<String> nombresTipos() {
        List<String> nombres = new ArrayList<>();
        for (TipoActividad tipo : tipos) {
            nombres.add(tipo.getNombre());
        }
        return nombres;
    }

    private void seleccionar(Spinner spinner, List<?> items, int idBuscado) {
        if (idBuscado < 1 || items.isEmpty() || spinner.getAdapter() == null) {
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            int id = items.get(i) instanceof Causa
                    ? ((Causa) items.get(i)).getId()
                    : ((TipoActividad) items.get(i)).getId();
            if (id == idBuscado) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private static int indiceEstado(String codigo) {
        if ("ACE".equals(codigo)) {
            return 1;
        }
        if ("REC".equals(codigo)) {
            return 2;
        }
        return 0;
    }

    private static String valor(EditText edit) {
        return edit.getText() == null ? "" : edit.getText().toString().trim();
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor;
    }
}
