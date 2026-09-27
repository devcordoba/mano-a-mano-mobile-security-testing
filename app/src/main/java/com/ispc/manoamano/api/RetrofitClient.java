package com.ispc.manoamano.api;

import android.content.Context;

import com.ispc.manoamano.BuildConfig;
import com.ispc.manoamano.almacenamiento.SessionManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {

        if (retrofit == null) {

            SessionManager sessionManager =
                    new SessionManager(context);

            Interceptor authInterceptor = chain -> {

                Request request = chain.request();

                String token =
                        sessionManager.obtenerToken();

                if (token != null && !token.isEmpty()) {

                    request = request.newBuilder()
                            .addHeader(
                                    "Authorization",
                                    "Bearer " + token
                            )
                            .build();
                }

                return chain.proceed(request);
            };

            OkHttpClient client =
                    new OkHttpClient.Builder()
                            .addInterceptor(authInterceptor)
                            .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.MANO_API_BASE_URL)
                    .client(client)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    )
                    .build();
        }

        return retrofit;
    }

    public static String normalizarUrlRecurso(String url) {
        if (url == null || !BuildConfig.DEBUG) {
            return url;
        }
        return url
                .replace("http://127.0.0.1:8000/", BuildConfig.MANO_API_BASE_URL)
                .replace("http://localhost:8000/", BuildConfig.MANO_API_BASE_URL);
    }
}
