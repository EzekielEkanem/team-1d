package edu.vassar.cmpu203.vassareats.model;

import static org.junit.Assert.*;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DiningStationTest {
    /**
     * Tests toSring() with different amounts of food items in the list
     */
    @Test
    public void testToString() {
        DiningStation diningStation = new DiningStation("Stocks");
        String expectedValue1 = "          " + "Stocks" + "                   \n" +
                "********************************************\n";
        String expectedValue2 = expectedValue1 + "Food id: " + "27444491" + "\n" + "Food name: " + "oatmeal" + "\n" + "Dietary labels: " + "[Vegan]" + "\n" + "*****************************\n";
        String expectedValue3 = expectedValue1 + "Food id: " + "27444491" + "\n" + "Food name: " + "oatmeal" + "\n" + "Dietary labels: " + "[Vegan]" + "\n" + "*****************************\n" + "Food id: " + "27444491" + "\n" + "Food name: " + "oatmeal" + "\n" + "Dietary labels: " + "[]" + "\n" + "*****************************\n";

        assertEquals(expectedValue1, diningStation.toString());

        HashSet<String> dietLabels = new HashSet<String>();
        dietLabels.add("Vegan");
        FoodItem foodItem = new FoodItem("oatmeal", "27444491", dietLabels);
        diningStation.addFoodItem(foodItem);

        assertEquals(expectedValue2, diningStation.toString());

        FoodItem foodItem2 = new FoodItem("oatmeal", "27444491", new HashSet<String>());
        diningStation.addFoodItem(foodItem2);

        assertEquals(expectedValue3, diningStation.toString());
    }

    /**
     * Tests addFoodItem() by adding a food item and checking if it was added through the toString()
     */
    @Test
    public void testAddFoodItem() {
        DiningStation diningStation = new DiningStation("Stocks");
        String expectedValue1 = "          " + "Stocks" + "                   \n" +
                "********************************************\n";
        String expectedValue2 = expectedValue1 + "Food id: " + "27444491" + "\n" + "Food name: " + "oatmeal" + "\n" + "Dietary labels: " + "[Vegan]" + "\n" + "*****************************\n";

        assertEquals(expectedValue1, diningStation.toString());

        HashSet<String> dietLabels = new HashSet<String>();
        dietLabels.add("Vegan");
        FoodItem foodItem = new FoodItem("oatmeal", "27444491", dietLabels);
        diningStation.addFoodItem(foodItem);

        assertEquals(expectedValue2, diningStation.toString());
    }
}