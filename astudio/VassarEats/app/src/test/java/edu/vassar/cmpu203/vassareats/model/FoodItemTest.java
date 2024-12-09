package edu.vassar.cmpu203.vassareats.model;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.HashSet;

public class FoodItemTest extends TestCase {

    /**
     * Tests the toString() method with different food items with different dietary labels
     */
    @Test
    public void testTestToString() {
        FoodItem foodItem = new FoodItem("oatmeal", "27444491", new HashSet<String>());
        String expectedValue = "Food id: " + "27444491" + "\n" + "Food name: " + "oatmeal" + "\n" + "Dietary labels: " + "[]";
        assertEquals(expectedValue, foodItem.toString());

        HashSet<String> dietLabels = new HashSet<String>();
        dietLabels.add("Vegan");
        FoodItem foodItem2 = new FoodItem("oatmeal", "27444491", dietLabels);
        String expectedValue2 = "Food id: " + "27444491" + "\n" + "Food name: " + "oatmeal" + "\n" + "Dietary labels: " + "[Vegan]";
        assertEquals(expectedValue2, foodItem2.toString());

        HashSet<String> dietLabels2 = new HashSet<String>();
        dietLabels2.add("Vegan");
        dietLabels2.add("Vegetarian");
        FoodItem foodItem3 = new FoodItem("oatmeal", "27444491", dietLabels2);
        String expectedValue3 = "Food id: " + "27444491" + "\n" + "Food name: " + "oatmeal" + "\n" + "Dietary labels: " + "[Vegetarian, Vegan]";
        assertEquals(expectedValue3, foodItem3.toString());
    }
}