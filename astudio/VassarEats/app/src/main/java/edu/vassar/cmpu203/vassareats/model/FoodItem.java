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

    public FoodItem(String name, String id, HashSet<String> dietLabels){
        this.name = name;
        this.id = id;
        this.dietLabels = dietLabels;
    }

    public String getFoodItemName(){
        return name;
    }

    public String getFoodId(){
        return id;
    }

    public HashSet<String> getDietLabels(){
        return dietLabels;
    }

    public String toString(){
        return "Food id: " + this.id + "\n" + "Food name: " + this.name + "\n" + "Dietary labels: " + this.dietLabels;
    }
}
