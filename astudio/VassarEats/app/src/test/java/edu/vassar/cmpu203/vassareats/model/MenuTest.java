package edu.vassar.cmpu203.vassareats.model;

import junit.framework.TestCase;

import org.json.JSONException;
import org.junit.Test;

import java.text.ParseException;
import java.time.LocalDate;
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
     * Tests the toString() method by checking if the string contains the correct menu items
     */
    @Test
    public void testTestToString() throws JSONException, ParseException {
        Menu menu = new Menu();

        String dateString = "2024-12-13";

        // Convert the string to a LocalDate
        LocalDate localDate = LocalDate.parse(dateString);

        menu.updateDate(localDate);

        assertTrue(menu.toString().contains("oatmeal"));
        assertTrue(menu.toString().contains("cream of rice"));
        assertTrue(menu.toString().contains("cinnamon and vanilla french toast stuffed with blueberry cream cheese"));
    }

    /**
     * Tests the updateDate() method by checking if the menus are different after updating the date
     */
    @Test
    public void testUpdateDate() throws JSONException, ParseException {
        Menu menu = new Menu();

        String dateString1 = "2024-12-12";
        String dateString2 = "2024-12-13";

        // Convert the string to a LocalDate
        LocalDate localDate1 = LocalDate.parse(dateString1);
        LocalDate localDate2 = LocalDate.parse(dateString2);

        menu.updateDate(localDate1);

        String menu1 = menu.getMenu().toString();

        menu.updateDate(localDate2);

        String menu2 = menu.getMenu().toString();

        assertTrue(menu1.contains("cream of wheat"));
        assertFalse(menu2.contains("cream of wheat"));
    }

    /**
     * Tests the updateLocation() method by checking if the menus are different after updating the
     * dining location
     */
    @Test
    public void testUpdateLocation() throws JSONException, ParseException {
        Menu menu = new Menu();

        Integer location1 = 1;
        Integer location2 = 2;

        menu.updateLocation(location1);

        String menu1 = menu.getMenu().toString();

        menu.updateLocation(location2);

        String menu2 = menu.getMenu().toString();

        assertTrue(menu1.contains("Express Specials"));
        assertTrue(menu2.contains("Toppings"));
    }
}