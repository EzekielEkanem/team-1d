package intellij.src;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.*;

import static java.lang.System.out;

public class Menu {
    private JSONObject jsonMenuObject;
    private HashMap<Integer, JSONObject> mealDayParts;
    private HashMap<String, Object> FoodItems = new HashMap<String, Object>();
    private Preference preference;

    public Menu(String urlStr, Preference preference) throws ParseException {
        Request request = new Request(urlStr);
        request.getWebPage();
        this.jsonMenuObject = request.getJsonMenu();
        this.mealDayParts = request.getMealDayParts();
        this.preference = preference;
    }

    public void getMenu() {
        out.println("**************************************************************************");
        out.println("                               Menu Items                                 ");
        out.println("**************************************************************************");
        for (Object key : jsonMenuObject.keySet()) {
            String keyStr = (String) key;
            JSONObject value = (JSONObject) jsonMenuObject.get(keyStr);

            FoodItem newFood = new FoodItem(keyStr, value);

            out.println(newFood);
            out.println("**************************************************************************");
        }
    }

    public void getMenu2() {
        out.println("**************************************************************************");
        out.println("                               Menu Items                                 ");
        out.println("**************************************************************************");
        for (Object key : mealDayParts.keySet()) {
            int keyStr = (int) key;
            JSONObject value = (JSONObject) mealDayParts.get(keyStr);

            MealType mealType = new MealType(keyStr, value, jsonMenuObject, preference);

            mealType.getMealType();
            out.println("**************************************************************************");
        }
    }
}
