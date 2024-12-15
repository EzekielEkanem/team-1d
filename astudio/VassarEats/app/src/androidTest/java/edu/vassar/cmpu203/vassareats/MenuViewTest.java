package edu.vassar.cmpu203.vassareats;

import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.Matchers;
import org.junit.Test;

public class MenuViewTest {

    @Test
    public void testMenuCreationAndDisplay() {
        // Launch the activity using ActivityScenario
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {

            Espresso.onView(ViewMatchers.withId(R.id.preference)).perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withText("Apply")).perform(ViewActions.click());

            SystemClock.sleep(1000);

            // Verify the menu is displayed in the UI
            Espresso.onView(ViewMatchers.withTagValue(Matchers.is("mealType_1")))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withTagValue(Matchers.is("mealTypeSection_1")))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withTagValue(Matchers.is("diningStation_1")))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withTagValue(Matchers.is("foodItem_1")))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void testMenuUpdateAndDisplay() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            Espresso.onView(ViewMatchers.withId(R.id.preference)).perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withText("Apply")).perform(ViewActions.click());

            SystemClock.sleep(1000);

            // Verify the menu is displayed in the UI
            Espresso.onView(ViewMatchers.withTagValue(Matchers.is("mealType_1")))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withTagValue(Matchers.is("mealTypeSection_1")))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withTagValue(Matchers.is("diningStation_1")))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withTagValue(Matchers.is("foodItem_1")))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.preference)).perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withText("Farm to Fork")).perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withText("Apply")).perform(ViewActions.click());

            SystemClock.sleep(1000);

            Espresso.onView(ViewMatchers.withTagValue(Matchers.is("foodItem_6")))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withTagValue(Matchers.is("foodItem_8")))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void testMenuScroll() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            Espresso.onView(ViewMatchers.withId(R.id.preference)).perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withText("Farm to Fork")).perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withText("Apply")).perform(ViewActions.click());

            SystemClock.sleep(1000);

            // Verify the menu is displayed in the UI
            Espresso.onView(ViewMatchers.withTagValue(Matchers.is("mealType_4")))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void testMenuClearAllButton() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            Espresso.onView(ViewMatchers.withId(R.id.preference)).perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withText("Farm to Fork")).perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withText("Apply")).perform(ViewActions.click());

            SystemClock.sleep(1000);

            Espresso.onView(ViewMatchers.withId(R.id.preference)).perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withText("Clear All")).perform(ViewActions.click());

            SystemClock.sleep(1000);

            Espresso.onView(ViewMatchers.withTagValue(Matchers.is("mealType_1")))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withTagValue(Matchers.is("mealTypeSection_1")))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withTagValue(Matchers.is("diningStation_1")))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withTagValue(Matchers.is("foodItem_1")))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void testMenuCancelButton() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            Espresso.onView(ViewMatchers.withId(R.id.preference)).perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withText("Farm to Fork")).perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withText("Apply")).perform(ViewActions.click());

            SystemClock.sleep(1000);

            Espresso.onView(ViewMatchers.withId(R.id.preference)).perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withText("Farm to Fork")).perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withText("Cancel")).perform(ViewActions.click());

            SystemClock.sleep(1000);

            // Verify the menu is displayed in the UI
            Espresso.onView(ViewMatchers.withTagValue(Matchers.is("mealType_4")))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }
}
