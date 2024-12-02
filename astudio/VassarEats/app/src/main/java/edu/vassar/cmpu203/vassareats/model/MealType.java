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
<<<<<<< HEAD
    private edu.vassar.cmpu203.vassareats.model.Preference preference;
    private ArrayList<edu.vassar.cmpu203.vassareats.model.MealTypeSection> mealTypeSections = new ArrayList<edu.vassar.cmpu203.vassareats.model.MealTypeSection>();
=======
    private Preference preference;
    private ArrayList<MealTypeSection> mealTypeSections = new ArrayList<MealTypeSection>();
    private String mealTypeNameReal;
>>>>>>> 2293aa9737e09b9836ea25153db9fd87130f929b

    /**
     * MealType constructor instantiates keyStr, value, jsonMenuObject, mealTypeName, mealTypeSection and
     * preference field variables. It also puts the corresponding section for each meal Type in
     * mealTypeName
     * @param keyStr: the integer that corresponds to a particular meal type (e.g. Breakfast)
     * @param value: JSON Object that contains the information for all food items for a particular meal type
     * @param jsonMenuObject: JSON Object that contains all information for all food items for a particular
     *                      day
     * @param preference: contains the preference selected by the user
     */
    public MealType(int keyStr, JSONObject value, JSONObject jsonMenuObject, edu.vassar.cmpu203.vassareats.model.Preference preference) {
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

<<<<<<< HEAD
    /**
     * setMealTypeSection method gets the stations of each meal and group them together in mealTypeSection
     * hashmap
     * @throws JSONException
     */
    public void setMealTypeSection() throws JSONException {
=======
    public MealType(String mealTypeName) {
        this.mealTypeNameReal = mealTypeName;
    }

    public void addMealTypeSection(MealTypeSection mealTypeSection) {
        mealTypeSections.add(mealTypeSection);
    }

    public void setMealTypeSection () throws JSONException {
>>>>>>> 2293aa9737e09b9836ea25153db9fd87130f929b

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

    /**
     * getMealTypeSections method returns a list of the meal type sections under a particular meal type
     * @return List<MealTypeSection>: the list of mealTypeSections classes
     */
    public List<edu.vassar.cmpu203.vassareats.model.MealTypeSection> getMealTypeSections() {
        return mealTypeSections;
    }

    /**
     * getMealTypeName method returns the meal type
     * @return String: the meal type (e.g., Dinner, Breakfast)
     */
    public String getMealTypeName() {
        return mealTypeNameReal;
    }

    /**
     * getMealType method separates the food items based on the meal type sections and appends each
     * mealTypeSection class into mealTypeSections list
     * @throws JSONException
     */
    public void getMealType() throws JSONException {
        String mealType = mealTypeName.get(keyStr);

        mealTypeSections.clear();

        setMealTypeSection();

        char[] tiers = {'1', '2', '3'};

        for (char tier : tiers) {
            String tierStr = String.valueOf(tier);
            HashSet<HashMap<String, JSONObject>> foodItems = mealTypeSection.get(tierStr);
            edu.vassar.cmpu203.vassareats.model.MealTypeSection mealSection = new edu.vassar.cmpu203.vassareats.model.MealTypeSection(tierStr, foodItems, mealType, preference);
            mealTypeSections.add(mealSection);
            mealSection.getMealTypeSection();
        }

    }

    /**
     * toString method returns a string representation of meal type
     * @return String: a string representation of meal type
     */
    public String toString () {
        String returnString = "";

        returnString += "                               " + mealTypeNameReal + "                                 \n";
        returnString += "**************************************************************************\n";

        for (edu.vassar.cmpu203.vassareats.model.MealTypeSection mealTypeSection : mealTypeSections) {
            returnString += mealTypeSection.toString();
            returnString += "***********************************************************\n";
        }

        return returnString;
    }
}
