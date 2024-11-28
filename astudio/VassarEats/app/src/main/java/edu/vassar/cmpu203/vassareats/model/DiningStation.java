package edu.vassar.cmpu203.vassareats.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DiningStation {
    private String stationName;
    private HashSet<HashMap<String, JSONObject>> foodItem;
    private ArrayList<String> preference;
    private ArrayList<FoodItem> foodItems = new ArrayList<FoodItem>();

    public DiningStation(String section, HashSet<HashMap<String, JSONObject>> foodItem, Preference preference) {
        this.stationName = section;
        this.foodItem = foodItem;
        this.preference = preference.getPreference();
    }

    public DiningStation(String stationName) {
        this.stationName = stationName;
    }

    public void addFoodItem(FoodItem foodItem) {
        foodItems.add(foodItem);
    }

    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    public String getDiningSectionName() {
        return stationName;
    }

    public void getDiningSection() throws JSONException {
        boolean printItem = false;

        foodItems.clear();

        for (HashMap<String, JSONObject> key : foodItem) {
            for (String keyName : key.keySet()) {
                FoodItem newFood = new FoodItem(keyName, key.get(keyName));
                HashSet<String> dietLabels = newFood.getDietLabels();
                for (String dietLabel : dietLabels) {
                    if (preference.contains(dietLabel) || preference.isEmpty()) {
                        printItem = true;
                    }
                }
                if (printItem) {
                    foodItems.add(newFood);
                }
                printItem = false;
            }
        }
    }

    public String toString() {
        String returnString = "";

        returnString += "          " + stationName + "                   \n" +
                "********************************************\n";

        for (FoodItem foodItem : foodItems) {
            returnString += foodItem.toString() + "\n";
            returnString += "*****************************\n";
        }

        return returnString;
    }
}
