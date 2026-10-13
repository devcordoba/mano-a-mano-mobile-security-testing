package com.ispc.manoamano.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.ispc.manoamano.modelos.LoginRequest;
import com.ispc.manoamano.modelos.LoginResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthApiMockWebServerTest {

    private MockWebServer server;
    private AuthApi authApi;

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        authApi = retrofit.create(AuthApi.class);
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void login_cuandoCredencialesValidas_devuelve200YToken() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"token\":\"jwt-demo\",\"user\":{\"id\":1,\"username\":\"ana_voluntaria\"}}"));

        Response<LoginResponse> response = authApi.login(
                new LoginRequest("ana_voluntaria", "Mano2026!")
        ).execute();

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals("jwt-demo", response.body().getToken());

        RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);
        assertEquals("POST", request.getMethod());
        assertEquals("/api/auth/login/", request.getPath());
    }

    @Test
    public void login_cuandoCredencialesInvalidas_devuelve401() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(401)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"detail\":\"Usuario y/o contraseña incorrecta.\"}"));

        Response<LoginResponse> response = authApi.login(
                new LoginRequest("x", "y")
        ).execute();

        assertEquals(401, response.code());
    }

    @Test
    public void login_cuandoFaltanCampos_devuelve400() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"detail\":\"Se requiere username y password.\"}"));

        Response<LoginResponse> response = authApi.login(
                new LoginRequest("", "")
        ).execute();

        assertEquals(400, response.code());
    }

    @Test
    public void login_cuandoElServidorResponde500_propagaErrorHttp() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(500));

        Response<LoginResponse> response = authApi.login(
                new LoginRequest("ana_voluntaria", "Mano2026!")
        ).execute();

        assertEquals(500, response.code());
    }

    @Test
    public void login_cuandoHayTimeout_fallaLaEjecucion() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBodyDelay(5, TimeUnit.SECONDS)
                .setBody("{\"token\":\"t\",\"user\":{\"id\":1,\"username\":\"u\"}}"));

        Retrofit retrofitCorto = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .client(new okhttp3.OkHttpClient.Builder()
                        .readTimeout(500, TimeUnit.MILLISECONDS)
                        .build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AuthApi apiCorta = retrofitCorto.create(AuthApi.class);

        try {
            apiCorta.login(new LoginRequest("u", "p")).execute();
        } catch (IOException ex) {
            assertNotNull(ex.getMessage());
            return;
        }
        throw new AssertionError("Se esperaba IOException por timeout");
    }
}
