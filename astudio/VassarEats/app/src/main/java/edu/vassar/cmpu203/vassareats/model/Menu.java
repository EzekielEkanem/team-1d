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
    private String menuURL = "https://vassar.cafebonappetit.com/cafe/gordon/2024-11-22/";
    private Preference preference;
    private List<MealType> originalMenu;

    /**
     *
     * @throws ParseException
     * @throws JSONException
     */
    public Menu() throws ParseException, JSONException {
        Request request = new Request();

        originalMenu = request.getJavaMenu(menuURL);
        this.preference = new Preference();
    }

    public List<MealType> getMenu() {
        return originalMenu;
    }

    public void changePreferences(List<Preference.Preferences> preferences) {
        preference.setPreferences(preferences);
    }

    public List<String> getPreferences() {
        return preference.getPreference();
    }

    public String toString () {
        String returnString = "";

        returnString += "**************************************************************************\n";
        returnString += "                               Menu Items                                 \n";
        returnString += "**************************************************************************\n";

        for (MealType mealType : originalMenu) {
            returnString += mealType.toString() + "\n";
            returnString += "**************************************************************************\n";
        }

        return returnString;
    }
}
