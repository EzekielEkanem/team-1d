package edu.vassar.cmpu203.vassareats.model;

import junit.framework.TestCase;

import org.json.JSONException;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MenuTest extends TestCase {
    /**
     * Tests the changePreferences() method by changing the preferences and checking if its correct
     */
    @Test
    public void testChangePreferences() throws JSONException, ParseException {
        Menu menu = new Menu();

        Preference preference = new Preference();
        List<Preference.Preferences> preferences = new ArrayList<Preference.Preferences>();
        preferences.add(Preference.Preferences.Vegan);

        menu.changePreferences(preferences);

        List<String> expectedValue = new ArrayList<String>();
        expectedValue.add("Vegan");

        assertEquals(expectedValue, menu.getPreferences());
    }

    /**
     * Tests the toString() method with the beginning of the menu
     */
    @Test
    public void testTestToString() throws JSONException, ParseException {
        Menu menu = new Menu();

        assertTrue(menu.toString().contains("**************************************************************************\n" +
                "                               Menu Items                                 \n" +
                "**************************************************************************\n" +
                "                               Breakfast                                 \n" +
                "**************************************************************************\n" +
                "              Breakfast Specials                          \n" +
                "***********************************************************\n" +
                "          Pressed                   \n" +
                "********************************************\n" +
                "Food id: 26946941\n" +
                "Food name: make your own belgian waffle\n" +
                "Dietary labels: [Vegetarian]\n" +
                "*****************************"));
    }
}