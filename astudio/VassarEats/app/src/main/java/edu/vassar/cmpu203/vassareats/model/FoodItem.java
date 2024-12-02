package edu.vassar.cmpu203.vassareats.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FoodItem {
    private String id;
    private String name;
    private HashSet<String> dietLabels;

    /**
     * FoodItem constructor instantiates id, name, and dietLabels field variables. It adds the appropriate
     * label(s) to dietLabels hashset
     * @param id: the id of the food item
     * @param value: a json object that contains all the information of the food item
     * @throws JSONException
     */
    public FoodItem(String id, JSONObject value) throws JSONException {
        this.id = id;
        this.name = (String) value.get("label");
        this.dietLabels = new HashSet<String>();

        if (value.get("cor_icon") instanceof JSONObject) {
            JSONObject icons = (JSONObject) value.get("cor_icon");
            for (Iterator<String> it = icons.keys(); it.hasNext();) {
                Object key = it.next();
                dietLabels.add((String) icons.get((String) key));
            }
        }
    }

<<<<<<< HEAD
    /**
     * getFoodItemName method returns the name of the food item based on the label on the json Object
     * @return String: the name of the food item
     */
=======
    public FoodItem(String name, String id, HashSet<String> dietLabels){
        this.name = name;
        this.id = id;
        this.dietLabels = dietLabels;
    }

>>>>>>> 2293aa9737e09b9836ea25153db9fd87130f929b
    public String getFoodItemName(){
        return name;
    }

<<<<<<< HEAD
    /**
     * getDietLabels method returns the dietLabels in which the food item is classified under (e.g. Vegan)
     * @return HashSet<String>: an hashset containing all dietary labels of the food
     */
=======
    public String getFoodId(){
        return id;
    }

>>>>>>> 2293aa9737e09b9836ea25153db9fd87130f929b
    public HashSet<String> getDietLabels(){
        return dietLabels;
    }

    /**
     * toString method returns a string representation of food item
     * @return String: a string representation of food item
     */
    public String toString(){
        return "Food id: " + this.id + "\n" + "Food name: " + this.name + "\n" + "Dietary labels: " + this.dietLabels;
    }
}