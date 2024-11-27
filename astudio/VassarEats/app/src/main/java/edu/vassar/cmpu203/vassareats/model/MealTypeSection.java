package edu.vassar.cmpu203.vassareats.model;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MealTypeSection {
    private String tierStr;
    private HashSet<HashMap<String, JSONObject>> foodItems;
    private String mealType;
    private HashMap<String, String> sectionNameHashMap;
    private HashMap<String, HashSet<HashMap<String, JSONObject>>> diningSectionHashMap;
    private edu.vassar.cmpu203.vassareats.model.Preference preference;
    private ArrayList<edu.vassar.cmpu203.vassareats.model.DiningSection> diningStations = new ArrayList<edu.vassar.cmpu203.vassareats.model.DiningSection>();

    /**
     * MealTypeSection constructor instantiates tierStr, foodItems, mealType, sectionNameHashMap, and
     * preference field variables. It also puts the corresponding section for each tierStr in
     * sectionNameHashMap
     * @param tierStr: this denotes the meal type section (e.g. Breakfast Specials)
     * @param foodItems: a hashset containing a hashmap of each food item. Each hashmap contains the
     *                   id of the food and the JSONObject containing all the information of the food
     * @param mealType: the meal type of the day (e.g., Breakfast, Lunch)
     * @param preference: the preference class that contains the preference selected by the user
     */
    public MealTypeSection(String tierStr, HashSet<HashMap<String, JSONObject>> foodItems, String mealType, edu.vassar.cmpu203.vassareats.model.Preference preference) {
        this.tierStr = tierStr;
        this.foodItems = foodItems;
        this.mealType = mealType;
        this.sectionNameHashMap = new HashMap<>();
        sectionNameHashMap.put("1", mealType + " Specials");
        sectionNameHashMap.put("2", "Additional " + mealType + " Favorites");
        sectionNameHashMap.put("3", mealType + " Condiments and Extras");
        this.diningSectionHashMap = new HashMap<>();
        this.preference = preference;
    }

    /**
     * getDiningSections method returns the list of the dining sections for a particular mealTypeSection
     * @return List<DiningSection>: the list containing the dining sections for a particular mealTypeSection
     */
    public List<edu.vassar.cmpu203.vassareats.model.DiningSection> getDiningSections() {
        return diningStations;
    }

    /**
     * getMealTypeSectionName method returns the meal type section
     * @return String: the meal type section
     */
    public String getMealTypeSectionName() {
        return sectionNameHashMap.get(tierStr);
    }

    /**
     * setDiningSectionHashMap extracts the dining stations for each food item and merges each food
     * item with their corresponding stations in a hashmap
     * @throws JSONException
     */
    public void setDiningSectionHashMap() throws JSONException {

        if (foodItems != null) {

            for (HashMap<String, JSONObject> key : foodItems) {
                for (String keyName : key.keySet()) {
                    String station = (String) key.get(keyName).get("station");
                    String[] stationsplit = station.split("@");
                    String[] stationsplit2 = stationsplit[1].split("<");

                    if (!diningSectionHashMap.containsKey(stationsplit2[0])) {
                        diningSectionHashMap.put(stationsplit2[0], new HashSet<HashMap<String, JSONObject>>());
                        diningSectionHashMap.get(stationsplit2[0]).add(key);
                    } else {
                        diningSectionHashMap.get(stationsplit2[0]).add(key);
                    }
                }
            }

        }
    }

    /**
     * getMealTypeSection method creates a diningSection class for each food item and add each diningSection
     * class to diningStations list
     * @throws JSONException
     */
    public void getMealTypeSection() throws JSONException {
        diningStations.clear();

        setDiningSectionHashMap();

        for (String key : diningSectionHashMap.keySet()) {
            edu.vassar.cmpu203.vassareats.model.DiningSection diningSection = new edu.vassar.cmpu203.vassareats.model.DiningSection(key, diningSectionHashMap.get(key), preference);
            diningStations.add(diningSection);
            diningSection.getDiningSection();
        }
    }

    /**
     * toString method returns a string representation of meal type section
     * @return String: a string representation of meal type section
     */
    public String toString() {
        String returnString = "";

        String sectionName = sectionNameHashMap.get(tierStr);
        returnString += "              " + sectionName + "                          \n";
        returnString += "***********************************************************\n";

        for (edu.vassar.cmpu203.vassareats.model.DiningSection station : diningStations) {
            returnString += station.toString();
            returnString += "********************************************\n";
        }

        return returnString;
    }
}
