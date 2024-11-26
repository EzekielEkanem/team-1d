package edu.vassar.cmpu203.vassareats.model;

import static java.lang.System.out;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Menu {
    private JSONObject jsonMenuObject;
    private HashMap<Integer, JSONObject> mealDayParts;
    private HashMap<String, Object> FoodItems = new HashMap<String, Object>();
    private String menuURL = "https://vassar.cafebonappetit.com/cafe/gordon/2024-11-22/";
    private Preference preference;
    private ArrayList<MealType> mealTypes = new ArrayList<MealType>();

    public Menu() throws ParseException, JSONException {
        Request request = new Request(menuURL);
        request.getWebPage();
        this.jsonMenuObject = request.getJsonMenu();
        this.mealDayParts = request.getMealDayParts();
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
        mealTypes.clear();

        for (Object key : mealDayParts.keySet()) {

            int keyStr = (int) key;
            Log.e("Testing", "Key: " + keyStr);
            JSONObject value = (JSONObject) mealDayParts.get(keyStr);

            MealType mealType = new MealType(keyStr, value, jsonMenuObject, preference);
            mealTypes.add(mealType);

            mealType.getMealType();

            Log.e("Testing", mealType.toString());
        }


    }

    public InputReport changePreferences(List preferences) {
        InputReport inputReport = new InputReport(true);
//        int preference;
//        ArrayList<Integer> preferencesList = new ArrayList<Integer>();
//
//        if (!preferences.isEmpty()) {
//            if (preferences.length() > 1) {
//                try {
//                    String[] preferencesArray = preferences.split(",");
//
//                    for (String p : preferencesArray) {
//                        preference = Integer.parseInt(p);
//
//                        if (preference <= 0 || preference >= 9) {
//                            inputReport.setStatusSuccess(false);
//                            inputReport.setErrorCode(0);
//                            break;
//                        }
//                        else {
//                            preferencesList.add(Integer.parseInt(p));
//                        }
//                    }
//                }
//                catch (Exception e) {
//                    inputReport.setStatusSuccess(false);
//                    inputReport.setErrorCode(1);
//                }
//            } else {
//                try{
//                    preference = Integer.parseInt(preferences);
//
//                    if (preference <= 0 || preference >= 9) {
//                        inputReport.setStatusSuccess(false);
//                        inputReport.setErrorCode(0);
//                    }
//                    else {
//                        preferencesList.add(preference);
//                    }
//                }
//                catch (Exception e) {
//                    inputReport.setStatusSuccess(false);
//                    inputReport.setErrorCode(1);
//                }
//            }
//        }
//        else {
//            inputReport.setStatusSuccess(false);
//            inputReport.setErrorCode(2);
//        }

        setMenuPreferences((ArrayList<Integer>) preferences);

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
