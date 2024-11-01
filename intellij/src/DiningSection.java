package intellij.src;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static java.lang.System.out;

public class DiningSection {

    private String section;
    private HashSet<HashMap<String, JSONObject>> foodItem;
    private ArrayList<String> preference;

    public DiningSection(String section, HashSet<HashMap<String, JSONObject>> foodItem, Preference preference) {
        this.section = section;
        this.foodItem = foodItem;
        this.preference = preference.getPreference();
    }

    public void getDiningSection() {
        out.println("          " + section + "                   ");
        out.println("********************************************");
        boolean printItem = false;

        for (HashMap<String, JSONObject> key : foodItem) {
            for (String keyName : key.keySet()) {
                FoodItem newFood = new FoodItem(keyName, key.get(keyName));
                HashSet<String> dietLabels = newFood.getDietLabels();
                for (String dietLabel : dietLabels) {
                    if (preference.contains(dietLabel) || preference.isEmpty()) {
                        printItem = true;
                    }
                }
                if (printItem) {
                    out.println(newFood);
                    out.println("*****************************");
                }
                printItem = false;
            }
        }
    }
}
