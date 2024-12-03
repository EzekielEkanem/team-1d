package edu.vassar.cmpu203.vassareats.model;

import static java.lang.System.out;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.vassar.cmpu203.vassareats.R;

public class Menu {
    private JSONObject jsonMenuObject;
    private HashMap<Integer, JSONObject> mealDayParts;
    private HashMap<String, Object> FoodItems = new HashMap<String, Object>();
    private String menuURL = "https://vassar.cafebonappetit.com/cafe/gordon/2024-11-22/";
    private Preference preference;
    private ArrayList<MealType> mealTypes = new ArrayList<MealType>();
    private List<MealType> originalMenu;
    private List<MealType> menu;

    public Menu() throws ParseException, JSONException {
        Request request = new Request();

        originalMenu = request.getJavaMenu(menuURL);
        menu = new ArrayList<MealType>();
        this.preference = new Preference(new ArrayList<Integer>());
    }

    public void getAllMenuItems() throws JSONException {
        out.println("**************************************************************************");
        out.println("                               Menu Items                                 ");
        out.println("**************************************************************************");
        for (Iterator<String> it = jsonMenuObject.keys(); it.hasNext(); ) {
            Object key = it.next();
            String keyStr = (String) key;
            JSONObject value = (JSONObject) jsonMenuObject.get(keyStr);

            FoodItem newFood = new FoodItem(keyStr, value);

            out.println(newFood);
            out.println("**************************************************************************");
        }
    }

    public void updateMenu() throws JSONException {
        menu.clear();

        for (MealType mealType : originalMenu) {

            MealType newMealType = new MealType(mealType.getMealTypeName());

            menu.add(newMealType);

            for (MealTypeSection mealTypeSection : mealType.getMealTypeSections()) {
                MealTypeSection newMealTypeSection = new MealTypeSection(mealTypeSection.getMealTypeSectionName());
                newMealType.addMealTypeSection(newMealTypeSection);
                for (DiningStation diningStation : mealTypeSection.getDiningSections()) {
                    DiningStation newDiningStation = new DiningStation(diningStation.getDiningSectionName());

                    newMealTypeSection.addDiningSection(newDiningStation);

                    for (FoodItem foodItem : diningStation.getFoodItems()) {

                        for (String dietLabel : foodItem.getDietLabels()) {
                            List<String> preferences = preference.getPreference();
                            if (preferences.contains(dietLabel) || preferences.isEmpty()) {
                                FoodItem newFood = new FoodItem(foodItem.getFoodItemName(), foodItem.getFoodId(), foodItem.getDietLabels());

                                newDiningStation.addFoodItem(newFood);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public List<MealType> getMenu() {
        return menu;
    }

    public void changePreferences(List<Integer> preferences) {
        this.preference = new Preference(preferences);
    }

    public void changeMenuURL() {

    }

    public String toString () {
        String returnString = "";

        returnString += "**************************************************************************\n";
        returnString += "                               Menu Items                                 \n";
        returnString += "**************************************************************************\n";

        for (MealType mealType : menu) {
            returnString += mealType.toString() + "\n";
            returnString += "**************************************************************************\n";
        }

        return returnString;
    }
}
