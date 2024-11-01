package intellij.src;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static java.lang.System.out;

public class MealTypeSection {
    private String tierStr;
    private HashSet<HashMap<String, JSONObject>> foodItems;
    private String mealType;
    private HashMap<String, String> sectionNameHashMap;
    private HashMap<String, HashSet<HashMap<String, JSONObject>>> diningSectionHashMap;
    private Preference preference;

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

    public void setDiningSectionHashMap() {

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

    public void getMealTypeSection() {

        String sectionName = sectionNameHashMap.get(tierStr);
        out.println("              " + sectionName + "                          ");
        out.println("***********************************************************");

        setDiningSectionHashMap();

        for (String key : diningSectionHashMap.keySet()) {
            DiningSection diningSection = new DiningSection(key, diningSectionHashMap.get(key), preference);
            diningSection.getDiningSection();
            out.println("********************************************");
        }
    }
}
