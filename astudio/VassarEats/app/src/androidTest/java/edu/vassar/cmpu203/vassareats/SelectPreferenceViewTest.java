package edu.vassar.cmpu203.vassareats;

import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.vassar.cmpu203.vassareats.MainActivity;
import edu.vassar.cmpu203.vassareats.model.DiningStation;
import edu.vassar.cmpu203.vassareats.model.FoodItem;
import edu.vassar.cmpu203.vassareats.model.MealType;
import edu.vassar.cmpu203.vassareats.model.MealTypeSection;
import edu.vassar.cmpu203.vassareats.model.Preference;

public class SelectPreferenceViewTest {

    @Test
    public void testHierarchicalMenuCreationAndDisplay() {
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
}
