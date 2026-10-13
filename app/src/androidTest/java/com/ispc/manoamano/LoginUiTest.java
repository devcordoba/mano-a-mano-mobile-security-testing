package com.ispc.manoamano;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ispc.manoamano.ui.LoginScreen;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.accessibility.AccessibilityChecks;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginUiTest {

    @BeforeClass
    public static void enableAccessibilityChecks() {
        AccessibilityChecks.enable();
    }

    @Test
    public void login_muestraCamposPrincipales() {
        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {
            LoginScreen.assertVisible();
        }
    }

    /** AUT-UI-02: flujo crítico navegación Login → Registro (Screen Object + Espresso). */
    @Test
    public void login_navegaARegistroDesdeEnlace() {
        try (ActivityScenario<LoginActivity> ignored =
                     ActivityScenario.launch(LoginActivity.class)) {
            LoginScreen.assertVisible();
            onView(withId(R.id.tvIrRegistroLogin)).perform(click());
            onView(withId(R.id.ETnombre_usuario)).check(matches(isDisplayed()));
            onView(withId(R.id.BTNregistrar)).check(matches(isDisplayed()));
        }
    }
}
