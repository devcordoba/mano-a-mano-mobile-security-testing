package com.ispc.manoamano.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import com.ispc.manoamano.R;

public final class LoginScreen {

    private LoginScreen() {
    }

    public static void assertVisible() {
        onView(withId(R.id.etUsuarioLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.etPasswordLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.btnIngresarLogin)).check(matches(isDisplayed()));
    }
}
