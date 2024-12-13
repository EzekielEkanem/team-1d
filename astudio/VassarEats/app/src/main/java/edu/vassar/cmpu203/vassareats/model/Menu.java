package edu.vassar.cmpu203.vassareats.model;

import static java.lang.System.out;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.vassar.cmpu203.vassareats.R;

public class Menu {
    private Preference preference;
    private List<MealType> originalMenu;
    private LocalDate currentDate;
    private Request request;

    /**
     *
     * @throws ParseException
     * @throws JSONException
     */
    public Menu() throws ParseException, JSONException {
        currentDate = LocalDate.now();
        request = new Request();
        preference = new Preference();

        originalMenu = request.getJavaMenu(currentDate);
    }

    public void updateDate(LocalDate localDate) throws JSONException, ParseException {
        if (!currentDate.equals(localDate)) {
            currentDate = localDate;
            originalMenu = request.getJavaMenu(currentDate);
        }
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
