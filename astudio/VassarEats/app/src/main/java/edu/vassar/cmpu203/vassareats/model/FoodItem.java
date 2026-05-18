package edu.vassar.cmpu203.vassareats.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class FoodItem {
    private String id;
    private String name;
    private HashSet<String> dietLabels;
    private HashMap<String, String> nutritionDetails;

    /**
     * FoodItem constructor instantiates id, name, and dietLabels field variables.
     * @param name: the name of the food item
     * @param id: the id of the food item
     * @param dietLabels: an hashset containing the dietlabels of the food item
     */
    public FoodItem(String name, String id, HashSet<String> dietLabels, HashMap<String, String> nutritionDetails){
        this.name = name;
        this.id = id;
        this.dietLabels = dietLabels;
        this.nutritionDetails = nutritionDetails;
    }

    /**
     * getFoodItemName method returns the name of the food item based on the label on the json Object
     * @return String: the name of the food item
     */
    public String getFoodItemName(){
        return name;
    }

    /**
     * getFoodId method returns the foodIDs of a particular food item
     * @return String: the food id
     */
    public String getFoodId(){
        return id;
    }

    /**
     * getDietLabels method returns the dietLabels in which the food item is classified under (e.g. Vegan)
     * @return HashSet<String>: an hashset containing all dietary labels of the food
     */
    public HashSet<String> getDietLabels(){
        return dietLabels;
    }

    public HashMap<String, String> getNutritionDetails() {
        return nutritionDetails;
    }

    public void setNutritionDetails(Map<String, String> generatedDetails) {
        this.nutritionDetails = (HashMap<String, String>) generatedDetails;
    }

    /**
     * toString method returns a string representation of food item
     * @return String: a string representation of food item
     */
    public String toString(){
        return "Food id: " + this.id + "\n" + "Food name: " + this.name + "\n" + "Dietary labels: " + this.dietLabels + "\nNutrition: " + this.nutritionDetails;
    }
}