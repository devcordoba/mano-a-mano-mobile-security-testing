package com.ispc.manoamano;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ispc.manoamano.ui.ContactScreen;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.accessibility.AccessibilityChecks;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.scrollTo;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ContactUiTest {

    @BeforeClass
    public static void enableAccessibilityChecks() {
        AccessibilityChecks.enable();
    }

    @Test
    public void contacto_muestraFormulario() {
        try (ActivityScenario<ContactActivity> scenario =
                     ActivityScenario.launch(ContactActivity.class)) {
            ContactScreen.assertVisible();
        }
    }

    /** AUT-UI-03: flujo crítico expandir formulario y completar campos (sin envío de red). */
    @Test
    public void contacto_expandeFormularioYCompletaCampos() {
        try (ActivityScenario<ContactActivity> ignored =
                     ActivityScenario.launch(ContactActivity.class)) {
            ContactScreen.assertVisible();
            onView(withId(R.id.btnContactanos)).perform(scrollTo(), click());
            onView(withId(R.id.editNombreContacto))
                    .perform(typeText("Equipo QA"), closeSoftKeyboard());
            onView(withId(R.id.editEmailContacto))
                    .perform(typeText("qa@ispc.edu.ar"), closeSoftKeyboard());
            onView(withId(R.id.editAsuntoContacto))
                    .perform(typeText("Consulta prueba"), closeSoftKeyboard());
            onView(withId(R.id.btnEnviarContacto)).check(matches(isDisplayed()));
        }
    }
}
