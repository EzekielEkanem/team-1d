package edu.vassar.cmpu203.vassareats.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Preference {
    private ArrayList<Integer> preferencesList;
    private HashMap<Integer, String> preferencesMap;
    private ArrayList<String> preferences;

    public Preference(ArrayList<Integer> preferencesList) {
        this.preferencesList = preferencesList;
        this.preferencesMap = new HashMap<>();
        preferencesMap.put(0, "Vegetarian");
        preferencesMap.put(1, "Vegan");
        preferencesMap.put(2, "Halal");
        preferencesMap.put(3, "Kosher");
        preferencesMap.put(4, "Made without Gluten-Containing Ingredients");
        preferencesMap.put(5, "Humane");
        preferencesMap.put(6, "Farm to Fork");
        this.preferences = new ArrayList<>();
    }

    public ArrayList<String> getPreference() {
        for (int i = 0; i < preferencesList.size(); i++) {
            if (preferencesMap.containsKey(preferencesList.get(i))) {
                preferences.add(preferencesMap.get(preferencesList.get(i)));
            }
        }
        return preferences;
    }
}
