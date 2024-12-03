package edu.vassar.cmpu203.vassareats.model;

import java.util.ArrayList;
import java.util.List;

public class MealType {
    private List<MealTypeSection> mealTypeSections = new ArrayList<edu.vassar.cmpu203.vassareats.model.MealTypeSection>();
    private String mealTypeName;

    /**
     * MealType constructor instantiates name of the mealType (e.g. Breakfast)
     * @param mealTypeName: the mealType of the food item
     */
    public MealType(String mealTypeName) {
        this.mealTypeName = mealTypeName;
    }

    /**
     * addMealTypeSection method adds the meal type section of the foodItems to the mealTypeSections arraylist
     * @param mealTypeSection: the mealTypeSection of the food items
     */
    public void addMealTypeSection(MealTypeSection mealTypeSection) {
        mealTypeSections.add(mealTypeSection);
    }

    /**
     * getMealTypeSections method returns a list of the meal type sections under a particular meal type
     * @return List<MealTypeSection>: the list of mealTypeSections classes
     */
    public List<MealTypeSection> getMealTypeSections() {
        return mealTypeSections;
    }

    /**
     * getMealTypeName method returns the meal type
     * @return String: the meal type (e.g., Dinner, Breakfast)
     */
    public String getMealTypeName() {
        return mealTypeName;
    }

    /**
     * toString method returns a string representation of meal type
     * @return String: a string representation of meal type
     */
    public String toString () {
        String returnString = "";

        returnString += "                               " + mealTypeName + "                                 \n";
        returnString += "**************************************************************************\n";

        for (edu.vassar.cmpu203.vassareats.model.MealTypeSection mealTypeSection : mealTypeSections) {
            returnString += mealTypeSection.toString();
            returnString += "***********************************************************\n";
        }

        return returnString;
    }
}
