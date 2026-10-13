package com.ispc.manoamano;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ispc.manoamano.ui.MainScreen;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.accessibility.AccessibilityChecks;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainUiTest {

    @BeforeClass
    public static void enableAccessibilityChecks() {
        AccessibilityChecks.enable();
    }

    /** AUT-UI-01: flujo crítico visitante en inicio (listado + bottom nav). */
    @Test
    public void main_muestraListadoYNavegacion() {
        try (ActivityScenario<MainActivity> scenario =
                     ActivityScenario.launch(MainActivity.class)) {
            MainScreen.assertVisible();
            MainScreen.assertBottomNavItemsVisible();
        }
    }
}
