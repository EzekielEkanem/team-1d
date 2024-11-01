package intellij.src;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static java.lang.System.out;

public class MealType {

    private int keyStr;
    private JSONObject value;
    private JSONObject jsonMenuObject;
    private HashMap<Integer, String> mealTypeName;
    private HashMap<String, HashSet<HashMap<String, JSONObject>>> mealTypeSection;
    private Preference preference;

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

    public void setMealTypeSection () {

        if (value.get("stations") instanceof JSONArray stations) {
            for (Object key : stations) {
                JSONObject station = (JSONObject) key;
                JSONArray items = (JSONArray) station.get("items");
                if (!items.isEmpty()) {
                    for (Object item : items) {
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

    public void getMealType() {

        String mealType = mealTypeName.get(keyStr);
        out.println("                               " + mealType + "                                 ");
        out.println("**************************************************************************");

        setMealTypeSection();

        char[] tiers = {'1', '2', '3'};

        for (char tier : tiers) {
            String tierStr = String.valueOf(tier);
            HashSet<HashMap<String, JSONObject>> foodItems = mealTypeSection.get(tierStr);
            MealTypeSection mealSection = new MealTypeSection(tierStr, foodItems, mealType, preference);
            mealSection.getMealTypeSection();
            out.println("***********************************************************");
        }

    }
}
