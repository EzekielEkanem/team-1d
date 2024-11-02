package intellij.src;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.*;

import static java.lang.System.out;

public class Menu {
    private JSONObject jsonMenuObject;
    private HashMap<Integer, JSONObject> mealDayParts;
    private HashMap<String, Object> FoodItems = new HashMap<String, Object>();
    private String menuURL = "https://vassar.cafebonappetit.com/cafe/gordon/2024-10-31/";
    private Preference preference;
    private ArrayList<MealType> mealTypes = new ArrayList<MealType>();

    public Menu() throws ParseException{
        Request request = new Request(menuURL);
        request.getWebPage();
        this.jsonMenuObject = request.getJsonMenu();
        this.mealDayParts = request.getMealDayParts();
        this.preference = new Preference(new ArrayList<Integer>());
    }

    public void getAllMenuItems() {
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

    public void updateMenu() {
        mealTypes.clear();

        for (Object key : mealDayParts.keySet()) {

            int keyStr = (int) key;
            JSONObject value = (JSONObject) mealDayParts.get(keyStr);

            MealType mealType = new MealType(keyStr, value, jsonMenuObject, preference);
            mealTypes.add(mealType);

            mealType.getMealType();
        }
    }

    public InputReport changePreferences(String preferences) {
        InputReport inputReport = new InputReport(true);
        int preference;
        ArrayList<Integer> preferencesList = new ArrayList<Integer>();

        if (!preferences.isEmpty()) {
            if (preferences.length() > 1) {
                try {
                    String[] preferencesArray = preferences.split(",");

                    for (String p : preferencesArray) {
                        preference = Integer.parseInt(p);

                        if (preference <= 0 || preference >= 9) {
                            inputReport.setStatusSuccess(false);
                            inputReport.setErrorCode(0);
                            break;
                        }
                        else {
                            preferencesList.add(Integer.parseInt(p));
                        }
                    }
                }
                catch (Exception e) {
                    inputReport.setStatusSuccess(false);
                    inputReport.setErrorCode(1);
                }
            } else {
                try{
                    preference = Integer.parseInt(preferences);

                    if (preference <= 0 || preference >= 9) {
                        inputReport.setStatusSuccess(false);
                        inputReport.setErrorCode(0);
                    }
                    else {
                        preferencesList.add(preference);
                    }
                }
                catch (Exception e) {
                    inputReport.setStatusSuccess(false);
                    inputReport.setErrorCode(1);
                }
            }
        }
        else {
            inputReport.setStatusSuccess(false);
            inputReport.setErrorCode(2);
        }

        setMenuPreferences(preferencesList);

        return inputReport;
    }

    private void setMenuPreferences(ArrayList<Integer> preferences){
        this.preference = new Preference(preferences);
    }

    public void changeMenuURL() {

    }

    public String toString () {
        String returnString = "";

        returnString += "**************************************************************************\n";
        returnString += "                               Menu Items                                 \n";
        returnString += "**************************************************************************\n";

        for (MealType mealType : mealTypes) {
            returnString += mealType.toString() + "\n";
            returnString += "**************************************************************************\n";
        }

        return returnString;
    }
}
