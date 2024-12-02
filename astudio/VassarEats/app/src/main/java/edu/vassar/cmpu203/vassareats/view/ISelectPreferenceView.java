package edu.vassar.cmpu203.vassareats.view;

import android.view.View;

import org.json.JSONException;

import java.text.ParseException;
import java.util.List;

import edu.vassar.cmpu203.vassareats.model.MealType;

public interface ISelectPreferenceView {
    interface Listener {
        void onAddPreferenceList(List<Integer> preferenceList) throws JSONException, ParseException;
    }

    void updateMenu(List<MealType> mealTypes);
    View getRootView();
}
