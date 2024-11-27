package edu.vassar.cmpu203.vassareats.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DiningSection {
    private String station;
    private HashSet<HashMap<String, JSONObject>> foodItem;
    private ArrayList<String> preference;
    private ArrayList<FoodItem> foodItems = new ArrayList<FoodItem>();

    public DiningSection(String section, HashSet<HashMap<String, JSONObject>> foodItem, Preference preference) {
        this.station = section;
        this.foodItem = foodItem;
        this.preference = preference.getPreference();
    }

    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    public String getDiningSectionName() {
        return station;
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

        returnString += "          " + station + "                   \n" +
                "********************************************\n";

        for (FoodItem foodItem : foodItems) {
            returnString += foodItem.toString() + "\n";
            returnString += "*****************************\n";
        }

        return returnString;
    }
}
