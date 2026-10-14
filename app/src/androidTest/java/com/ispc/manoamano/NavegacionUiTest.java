package com.ispc.manoamano;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

import android.app.Activity;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.ispc.manoamano.ui.ContactScreen;
import com.ispc.manoamano.ui.MainScreen;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NavegacionUiTest {

    @BeforeClass
    public static void enableAccessibilityChecks() {
        AccessibilityChecks.enable();
    }

    /**
     * TC-13: Inicio -> Contacto -> Inicio no debe dejar dos MainActivity en la pila.
     * Antes de NavegacionHelper, ContactActivity abría un Main nuevo y dejaba el original debajo.
     */
    @Test
    public void navegacion_inicioContactoInicio_noDuplicaMainActivity() {
        // Sin try-with-resources: el test navega entre varias Activities y
        // ActivityScenario.close() no puede seguir a la Activity original.
        ActivityScenario.launch(MainActivity.class);

        MainScreen.assertVisible();

        onView(withId(R.id.nav_contacto)).perform(click());
        ContactScreen.assertVisible();

        onView(withId(R.id.nav_inicio)).perform(click());
        MainScreen.assertVisible();

        assertEquals("Debe haber una sola MainActivity viva",
                1, contarActivitiesVivas(MainActivity.class));
    }

    private static int contarActivitiesVivas(Class<? extends Activity> clase) {
        int[] total = {0};
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            for (Stage etapa : Stage.values()) {
                if (etapa == Stage.DESTROYED || etapa == Stage.PRE_ON_CREATE) {
                    continue;
                }
                for (Activity a : ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(etapa)) {
                    if (clase.isInstance(a) && !a.isFinishing()) {
                        total[0]++;
                    }
                }
            }
        });
        return total[0];
    }
}
