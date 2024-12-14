package edu.vassar.cmpu203.vassareats.model;

import java.util.ArrayList;
import java.util.List;

public class DiningStation {
    private String stationName;
    private List<FoodItem> foodItems = new ArrayList<edu.vassar.cmpu203.vassareats.model.FoodItem>();

    /**
     * DiningStation constructor instantiates station section of the food item
     * @param stationName: the station section of the food item
     */
    public DiningStation(String stationName) {
        this.stationName = stationName;
    }

    /**
     * addFoodItem method adds foodItems to the foodItems arraylist
     * @param foodItem: the foodItem to be added to the list
     */
    public void addFoodItem(FoodItem foodItem) {
        foodItems.add(foodItem);
    }

    /**
     * getFoodItems method returns the list of the food items for a particular station
     * @return List<FoodItem>: the list containing the food items for a particular station
     */
    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    /**
     * getDiningSectionName method returns the station name for the food item
     * @return String: the station name of the food item
     */
    public String getDiningStationName() {
        return stationName;
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

