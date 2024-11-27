package edu.vassar.cmpu203.vassareats.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MealType {
    private int keyStr;
    private JSONObject value;
    private JSONObject jsonMenuObject;
    private HashMap<Integer, String> mealTypeName;
    private HashMap<String, HashSet<HashMap<String, JSONObject>>> mealTypeSection;
    private Preference preference;
    private ArrayList<MealTypeSection> mealTypeSections = new ArrayList<MealTypeSection>();

    public MealType(int keyStr, JSONObject value, JSONObject jsonMenuObject, Preference preference) {
        this.keyStr = keyStr;
        this.value = value;
        this.jsonMenuObject = jsonMenuObject;
        this.mealTypeName = new HashMap<Integer, String>();
        mealTypeName.put(1, "Breakfast");
        mealTypeName.put(2, "Brunch");
        mealTypeName.put(3, "Lunch");
        mealTypeName.put(739, "Light Lunch");
        mealTypeName.put(4, "Dinner");
        mealTypeName.put(7, "Late Night");
        this.mealTypeSection = new HashMap<String, HashSet<HashMap<String, JSONObject>>>();
        this.preference = preference;
    }

    public void setMealTypeSection () throws JSONException {

        if (value.get("stations") instanceof JSONArray stations) {
            for (int i = 0; i < stations.length(); i++) {
                Object key = stations.get(i);
                JSONObject station = (JSONObject) key;
                JSONArray items = (JSONArray) station.get("items");
                if (!(items.length() == 0)) {
                    for (int j = 0; j < items.length(); j++) {
                        Object item = items.get(j);
                        String itemName = (String) item;
                        JSONObject foodItem = (JSONObject) jsonMenuObject.get(itemName);
                        String tier = String.valueOf(foodItem.get("tier"));
                        HashMap<String, JSONObject> dishes = new HashMap<String, JSONObject>();
                        dishes.put(itemName, foodItem);
                        if (!mealTypeSection.containsKey(tier)) {
                            mealTypeSection.put(tier, new HashSet<HashMap<String, JSONObject>>());
                            mealTypeSection.get(tier).add(dishes);
                        } else {
                            mealTypeSection.get(tier).add(dishes);
                        }
                    }
                }
            }
        }
    }

    public List<MealTypeSection> getMealTypeSections() {
        return mealTypeSections;
    }

    public String getMealTypeName() {
        return mealTypeName.get(keyStr);
    }

    public void getMealType() throws JSONException {
        String mealType = mealTypeName.get(keyStr);

        mealTypeSections.clear();

        setMealTypeSection();

        char[] tiers = {'1', '2', '3'};

        for (char tier : tiers) {
            String tierStr = String.valueOf(tier);
            HashSet<HashMap<String, JSONObject>> foodItems = mealTypeSection.get(tierStr);
            MealTypeSection mealSection = new MealTypeSection(tierStr, foodItems, mealType, preference);
            mealTypeSections.add(mealSection);
            mealSection.getMealTypeSection();
        }

    }

    public String toString () {
        String returnString = "";

        String mealType = mealTypeName.get(keyStr);
        returnString += "                               " + mealType + "                                 \n";
        returnString += "**************************************************************************\n";

        for (MealTypeSection mealTypeSection : mealTypeSections) {
            returnString += mealTypeSection.toString();
            returnString += "***********************************************************\n";
        }

        return returnString;
    }
}
