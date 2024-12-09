package edu.vassar.cmpu203.vassareats.model;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.HashSet;

public class MealTypeSectionTest extends TestCase {

    /**
     * Tests the addDiningSection() method by adding a dining section and checking if it was added through the toString()
     */
    @Test
    public void testAddDiningStation() {
        MealTypeSection mealTypeSection = new MealTypeSection("Breakfast Specials");
        DiningStation diningStation = new DiningStation("Stocks");
        DiningStation diningStation2 = new DiningStation("Stocks");
        HashSet<String> dietLabels = new HashSet<String>();
        dietLabels.add("Vegan");
        FoodItem foodItem = new FoodItem("oatmeal", "27444491", dietLabels);
        diningStation.addFoodItem(foodItem);

        String expectedValue1 = "              " + "Breakfast Specials" + "                          \n" +
                "***********************************************************\n";
        String expectedValue2 = expectedValue1 + "          " + "Stocks" + "                   \n" +
                "********************************************\n" + "Food id: " + "27444491" + "\n" + "Food name: " + "oatmeal" + "\n" + "Dietary labels: " + "[Vegan]" + "\n" + "*****************************\n" + "********************************************\n";
        String expectedValue3 = expectedValue1 + "          " + "Stocks" + "                   \n" +
                "********************************************\n" + "Food id: " + "27444491" + "\n" + "Food name: " + "oatmeal" + "\n" + "Dietary labels: " + "[Vegan]" + "\n" + "*****************************\n" + "********************************************\n" + "          " + "Stocks" + "                   \n" +
                "********************************************\n" + "********************************************\n";



        assertEquals(expectedValue1, mealTypeSection.toString());

        mealTypeSection.addDiningStation(diningStation);

        assertEquals(expectedValue2, mealTypeSection.toString());

        mealTypeSection.addDiningStation(diningStation2);

        assertEquals(expectedValue3, mealTypeSection.toString());
    }

    /**
     * Tests the toString() method with different meal type section structures
     */
    @Test
    public void testTestToString() {
        MealTypeSection mealTypeSection = new MealTypeSection("Breakfast Specials");
        DiningStation diningStation = new DiningStation("Stocks");
        HashSet<String> dietLabels = new HashSet<String>();
        dietLabels.add("Vegan");
        FoodItem foodItem = new FoodItem("oatmeal", "27444491", dietLabels);
        diningStation.addFoodItem(foodItem);

        String expectedValue1 = "              " + "Breakfast Specials" + "                          \n" +
                "***********************************************************\n";
        String expectedValue2 = expectedValue1 + "          " + "Stocks" + "                   \n" +
                "********************************************\n" + "Food id: " + "27444491" + "\n" + "Food name: " + "oatmeal" + "\n" + "Dietary labels: " + "[Vegan]" + "\n" + "*****************************\n" + "********************************************\n";

        assertEquals(expectedValue1, mealTypeSection.toString());

        mealTypeSection.addDiningStation(diningStation);

        assertEquals(expectedValue2, mealTypeSection.toString());
    }
}