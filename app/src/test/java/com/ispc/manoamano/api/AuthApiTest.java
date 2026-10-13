package com.ispc.manoamano.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.ispc.manoamano.modelos.LoginRequest;
import com.ispc.manoamano.modelos.LoginResponse;
import com.ispc.manoamano.modelos.MeResponse;
import com.ispc.manoamano.utilidades.HttpErrorHandler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthApiTest {

    private static final String LOGIN_OK =
            "{\"user\":{\"id\":7,\"username\":\"ana_voluntaria\","
                    + "\"first_name\":\"Ana\",\"last_name\":\"Mendez\","
                    + "\"email\":\"ana.mendez@email.com\"},"
                    + "\"token\":\"abc.def.ghi\"}";

    private MockWebServer server;
    private AuthApi api;

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.SECONDS)
                .build();

        api = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthApi.class);
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    // AUT-NET-01: respuesta 200 bien parseada
    @Test
    public void login200_parseaTokenYUsuario() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(LOGIN_OK));

        Response<LoginResponse> respuesta =
                api.login(new LoginRequest("ana_voluntaria", "clave-de-prueba")).execute();

        assertTrue(respuesta.isSuccessful());
        LoginResponse cuerpo = respuesta.body();
        assertNotNull(cuerpo);
        assertEquals("abc.def.ghi", cuerpo.getToken());
        assertEquals(7, cuerpo.getUser().getId());
        assertEquals("ana_voluntaria", cuerpo.getUser().getUsername());
        assertEquals("Ana", cuerpo.getUser().getFirstName());
        assertEquals("ana.mendez@email.com", cuerpo.getUser().getEmail());
    }

    // El request que sale es el que espera el backend
    @Test
    public void login_enviaPostConUsuarioYPasswordEnJson() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(LOGIN_OK));

        api.login(new LoginRequest("ana_voluntaria", "clave-de-prueba")).execute();

        RecordedRequest enviado = server.takeRequest();
        assertEquals("POST", enviado.getMethod());
        assertEquals("/api/auth/login/", enviado.getPath());
        String cuerpo = enviado.getBody().readUtf8();
        assertTrue(cuerpo.contains("\"username\":\"ana_voluntaria\""));
        assertTrue(cuerpo.contains("\"password\":\"clave-de-prueba\""));
    }

    // AUT-NET-02: 401 credenciales invalidas
    @Test
    public void login401_noEsExitosoYTraeMensajeDeSesionInvalida() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401)
                .setBody("{\"detail\":\"Credenciales invalidas\"}"));

        Response<LoginResponse> respuesta =
                api.login(new LoginRequest("ana_voluntaria", "incorrecta")).execute();

        assertFalse(respuesta.isSuccessful());
        assertEquals(401, respuesta.code());
        assertEquals("Tu sesión no es válida o ha expirado.",
                HttpErrorHandler.obtenerMensaje(respuesta.code()));
    }

    // AUT-NET-02: 404
    @Test
    public void me404_noEsExitoso() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404));

        Response<MeResponse> respuesta = api.me().execute();

        assertFalse(respuesta.isSuccessful());
        assertEquals(404, respuesta.code());
        assertEquals("No se encontró el recurso solicitado.",
                HttpErrorHandler.obtenerMensaje(respuesta.code()));
    }

    // AUT-NET-03: 500 error de servidor
    @Test
    public void me500_noEsExitosoYUsaMensajeDeErrorDeServidor() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(500));

        Response<MeResponse> respuesta = api.me().execute();

        assertFalse(respuesta.isSuccessful());
        assertEquals(500, respuesta.code());
        assertEquals("Ocurrió un error en el servidor.",
                HttpErrorHandler.obtenerMensaje(respuesta.code()));
    }

    // AUT-NET-03: timeout (el servidor no responde)
    @Test
    public void login_siElServidorNoResponde_lanzaIOExceptionPorTimeout() {
        server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));

        try {
            api.login(new LoginRequest("ana_voluntaria", "clave-de-prueba")).execute();
            fail("Se esperaba un timeout");
        } catch (IOException esperado) {
            // OK: sin respuesta dentro del tiempo permitido
        }
    }
}
