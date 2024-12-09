package edu.vassar.cmpu203.vassareats.model;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PreferenceTest extends TestCase {

    /**
     * Tests the setPreferences() method by setting a preference and checking if it was set through getPreference()
     */
    @Test
    public void testSetPreferences() {
        Preference preference = new Preference();
        List<Preference.Preferences> preferences = new ArrayList<Preference.Preferences>();
        preferences.add(Preference.Preferences.Vegan);
        preference.setPreferences(preferences);

        List<String> expectedValue = new ArrayList<String>();
        expectedValue.add("Vegan");

        assertEquals(expectedValue, preference.getPreference());
    }
}