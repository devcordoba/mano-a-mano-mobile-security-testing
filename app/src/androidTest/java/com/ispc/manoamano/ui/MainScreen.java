package com.ispc.manoamano.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import com.ispc.manoamano.R;

public final class MainScreen {

    private MainScreen() {
    }

    public static void assertVisible() {
        onView(withId(R.id.recyclerOportunidades)).check(matches(isDisplayed()));
        onView(withId(R.id.bottomNavigation)).check(matches(isDisplayed()));
    }

    public static void assertBottomNavItemsVisible() {
        onView(withId(R.id.editBuscar)).check(matches(isDisplayed()));
        onView(withId(R.id.tvIniciarSesion)).check(matches(isDisplayed()));
    }
}
