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
    private ArrayList<edu.vassar.cmpu203.vassareats.model.FoodItem> foodItems = new ArrayList<edu.vassar.cmpu203.vassareats.model.FoodItem>();

<<<<<<< HEAD:astudio/VassarEats/app/src/main/java/edu/vassar/cmpu203/vassareats/model/DiningSection.java
    /**
     * DiningSection constructor instantiates station, foodItem, and preference field variables.
     * @param section: the station section of the food item
     * @param foodItem: a hashset containing a hashmap of each food item. Each hashmap contains the
     *                id of the food and the JSONObject containing all the information of the food
     * @param preference: the preference class that contains the preference selected by the user
     */
    public DiningSection(String section, HashSet<HashMap<String, JSONObject>> foodItem, edu.vassar.cmpu203.vassareats.model.Preference preference) {
        this.station = section;
=======
    public DiningStation(String section, HashSet<HashMap<String, JSONObject>> foodItem, Preference preference) {
        this.stationName = section;
>>>>>>> 2293aa9737e09b9836ea25153db9fd87130f929b:astudio/VassarEats/app/src/main/java/edu/vassar/cmpu203/vassareats/model/DiningStation.java
        this.foodItem = foodItem;
        this.preference = preference.getPreference();
    }

<<<<<<< HEAD:astudio/VassarEats/app/src/main/java/edu/vassar/cmpu203/vassareats/model/DiningSection.java
    /**
     * getFoodItems method returns the list of the food items for a particular station
     * @return List<FoodItem>: the list containing the food items for a particular station
     */
    public List<edu.vassar.cmpu203.vassareats.model.FoodItem> getFoodItems() {
=======
    public DiningStation(String stationName) {
        this.stationName = stationName;
    }

    public void addFoodItem(FoodItem foodItem) {
        foodItems.add(foodItem);
    }

    public List<FoodItem> getFoodItems() {
>>>>>>> 2293aa9737e09b9836ea25153db9fd87130f929b:astudio/VassarEats/app/src/main/java/edu/vassar/cmpu203/vassareats/model/DiningStation.java
        return foodItems;
    }

    /**
     * getDiningSectionName method returns the station name for the food item
     * @return String: the station name of the food item
     */
    public String getDiningSectionName() {
        return stationName;
    }

    /**
     * getDiningSection method creates a food item class for each method in foodItem and appends each
     * foodItem class to foodItems Array list
     * @throws JSONException
     */
    public void getDiningSection() throws JSONException {
        boolean printItem = false;

        foodItems.clear();

        for (HashMap<String, JSONObject> key : foodItem) {
            for (String keyName : key.keySet()) {
                edu.vassar.cmpu203.vassareats.model.FoodItem newFood = new edu.vassar.cmpu203.vassareats.model.FoodItem(keyName, key.get(keyName));
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

    /**
     * toString method returns a string representation of dining section
     * @return String: a string representation of dining section
     */
    public String toString() {
        String returnString = "";

        returnString += "          " + stationName + "                   \n" +
                "********************************************\n";

        for (edu.vassar.cmpu203.vassareats.model.FoodItem foodItem : foodItems) {
            returnString += foodItem.toString() + "\n";
            returnString += "*****************************\n";
        }

        return returnString;
    }
}
