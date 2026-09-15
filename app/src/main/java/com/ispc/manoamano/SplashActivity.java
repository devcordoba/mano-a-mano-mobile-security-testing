package com.ispc.manoamano;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 5 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Usar Handler para esperar 3 segundos y luego ir al Login
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Iniciar LoginActivity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finalizar SplashActivity para que no vuelva atrás
            }
        }, SPLASH_DURATION);
    }
}