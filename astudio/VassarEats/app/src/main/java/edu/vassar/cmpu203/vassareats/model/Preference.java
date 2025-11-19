package edu.vassar.cmpu203.vassareats.model;

import java.util.ArrayList;
import java.util.List;

public class Preference {
    private List<Preferences> preferences;
    public enum Preferences {
        Vegetarian("Vegetarian"),
        Vegan("Vegan"),
        Halal("Halal"),
        Kosher("Kosher"),
        MadeWithoutGluten("Made without Gluten-Containing Ingredients"),
        Humane("Humane"),
        FarmToFork("Farm to Fork");

        private final String preferenceName;

        // Constructor to initialize the course name
        Preferences(String preferenceName) {
            this.preferenceName = preferenceName;
        }

        public static Preferences getPreference(String preferenceName) {
            for (Preferences preference : Preferences.values()) {
                if (preference.preferenceName.equals(preferenceName)) {
                    return preference;
                }
            }
            return null;
        }

        // Override the toString() method
        @Override
        public String toString() {
            return preferenceName;
        }
    }

    public Preference() {
        preferences = new ArrayList<>();
    }

    public void setPreferences(List<Preferences> preferencesList) {
        preferences = preferencesList;
    }

    public ArrayList<String> getPreferences() {
        ArrayList<String> preferencesString = new ArrayList<>();

        for (Preferences preference : preferences) {
            preferencesString.add(preference.toString());
        }
        return preferencesString;
    }

    public void clearPreferences() {
        preferences.clear();
    }
}
