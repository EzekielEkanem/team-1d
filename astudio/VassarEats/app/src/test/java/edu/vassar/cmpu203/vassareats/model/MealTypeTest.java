package edu.vassar.cmpu203.vassareats.model;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.HashSet;

public class MealTypeTest extends TestCase {

    /**
     * Tests the addMealTypeSection() method by adding a meal type section and checking if it was added through the toString()
     */
    @Test
    public void testAddMealTypeSection() {
        MealType mealType = new MealType("Breakfast");
        MealTypeSection mealTypeSection = new MealTypeSection("Breakfast Specials");
        MealTypeSection mealTypeSection2 = new MealTypeSection("Breakfast Specials");
        DiningStation diningStation = new DiningStation("Stocks");
        HashSet<String> dietLabels = new HashSet<String>();
        dietLabels.add("Vegan");
        FoodItem foodItem = new FoodItem("oatmeal", "27444491", dietLabels);
        diningStation.addFoodItem(foodItem);

        String expectedValue1 = "              " + "Breakfast" + "                          \n" +
                "***********************************************************\n";
        String expectedValue2 = expectedValue1 + "              " + "Breakfast Specials" + "                          \n" + "          " + "Stocks" + "                   \n" +
                "********************************************\n" + "Food id: " + "27444491" + "\n" + "Food name: " + "oatmeal" + "\n" + "Dietary labels: " + "[Vegan]" + "\n" + "*****************************\n" + "********************************************\n";

        assertEquals(expectedValue1, mealType.toString());

        mealType.addMealTypeSection(mealTypeSection);

        assertEquals(expectedValue2, mealType.toString());
    }

    /**
     * Tests the toString() method with different meal type structures
     */
    @Test
    public void testTestToString() {
        MealType mealType = new MealType("Breakfast");
        MealTypeSection mealTypeSection = new MealTypeSection("Breakfast Specials");
        MealTypeSection mealTypeSection2 = new MealTypeSection("Breakfast Specials");
        DiningStation diningStation = new DiningStation("Stocks");
        HashSet<String> dietLabels = new HashSet<String>();
        dietLabels.add("Vegan");
        FoodItem foodItem = new FoodItem("oatmeal", "27444491", dietLabels);
        diningStation.addFoodItem(foodItem);

        String expectedValue1 = "              " + "Breakfast" + "                          \n" +
                "***********************************************************\n";
        String expectedValue2 = expectedValue1 + "              " + "Breakfast Specials" + "                          \n" + "          " + "Stocks" + "                   \n" +
                "********************************************\n" + "Food id: " + "27444491" + "\n" + "Food name: " + "oatmeal" + "\n" + "Dietary labels: " + "[Vegan]" + "\n" + "*****************************\n" + "********************************************\n";
        String expectedValue3 = expectedValue1 + "              " + "Breakfast Specials" + "                          \n" + "          " + "Stocks" + "                   \n" +
                "********************************************\n" + "Food id: " + "27444491" + "\n" + "Food name: " + "oatmeal" + "\n" + "Dietary labels: " + "[Vegan]" + "\n" + "*****************************\n" + "********************************************\n" + "***********************************************************\n" + "              " + "Breakfast Specials" + "                          \n" + "          " + "Stocks" + "                   \n" +
                "********************************************\n";

        assertEquals(expectedValue1, mealType.toString());

        mealType.addMealTypeSection(mealTypeSection);

        assertEquals(expectedValue2, mealType.toString());

        mealType.addMealTypeSection(mealTypeSection2);

        assertEquals(expectedValue3, mealType.toString());
    }
}