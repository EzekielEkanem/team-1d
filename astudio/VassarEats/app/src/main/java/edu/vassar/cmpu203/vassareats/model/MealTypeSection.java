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
    private Preference preference;
    private ArrayList<DiningSection> diningStations = new ArrayList<DiningSection>();

    public MealTypeSection(String tierStr, HashSet<HashMap<String, JSONObject>> foodItems, String mealType, Preference preference) {
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

    public List<DiningSection> getDiningSections() {
        return diningStations;
    }

    public String getMealTypeSectionName() {
        return sectionNameHashMap.get(tierStr);
    }

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

    public void getMealTypeSection() throws JSONException {
        diningStations.clear();

        setDiningSectionHashMap();

        for (String key : diningSectionHashMap.keySet()) {
            DiningSection diningSection = new DiningSection(key, diningSectionHashMap.get(key), preference);
            diningStations.add(diningSection);
            diningSection.getDiningSection();
        }
    }

    public String toString() {
        String returnString = "";

        String sectionName = sectionNameHashMap.get(tierStr);
        returnString += "              " + sectionName + "                          \n";
        returnString += "***********************************************************\n";

        for (DiningSection station : diningStations) {
            returnString += station.toString();
            returnString += "********************************************\n";
        }

        return returnString;
    }
}
