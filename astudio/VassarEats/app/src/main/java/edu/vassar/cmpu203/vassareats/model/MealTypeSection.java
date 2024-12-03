package edu.vassar.cmpu203.vassareats.model;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MealTypeSection {
    private List<DiningStation> diningStations = new ArrayList<DiningStation>();
    private String mealTypeSectionName;

    /**
     * MealTypeSection constructor instantiates name of the mealTypeSection (e.g. Breakfast Specials)
     * @param mealTypeSectionName: the name of the mealTypeSection
     */
    public MealTypeSection(String mealTypeSectionName) {
        this.mealTypeSectionName = mealTypeSectionName;
    }

    /**
     * addDiningSection method adds the dining station of the foodItems to the diningStations arraylist
     * @param diningStation: the dining station of the foodItems
     */
    public void addDiningSection(DiningStation diningStation) {
        diningStations.add(diningStation);
    }

    /**
     * getDiningSections method returns the list of the dining sections for a particular mealTypeSection
     * @return List<DiningSection>: the list containing the dining sections for a particular mealTypeSection
     */
    public List<DiningStation> getDiningSections() {

        return diningStations;
    }

    /**
     * getMealTypeSectionName method returns the meal type section
     * @return String: the meal type section
     */
    public String getMealTypeSectionName() {
        return mealTypeSectionName;
    }

    /**
     * toString method returns a string representation of meal type section
     * @return String: a string representation of meal type section
     */
    public String toString() {
        String returnString = "";

        returnString += "              " + mealTypeSectionName + "                          \n";
        returnString += "***********************************************************\n";

        for (DiningStation station : diningStations) {
            returnString += station.toString();
            returnString += "********************************************\n";
        }

        return returnString;
    }
}
