package edu.vassar.cmpu203.vassareats.view;

import android.view.View;

import org.json.JSONException;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

import edu.vassar.cmpu203.vassareats.model.MealType;
import edu.vassar.cmpu203.vassareats.model.Preference;

public interface IMenuView {
    interface Listener {
        void updatePreferences(List<Preference.Preferences> preferenceList);
        void updateDate(LocalDate localDate) throws JSONException, ParseException;
    }

    void updateMenu(List<MealType> mealTypes, List<String> preferences);
    View getRootView();
}
