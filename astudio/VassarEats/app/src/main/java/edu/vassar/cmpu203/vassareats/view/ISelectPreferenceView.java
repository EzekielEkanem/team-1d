package edu.vassar.cmpu203.vassareats.view;

import android.view.View;

import org.json.JSONException;

import java.text.ParseException;
import java.util.List;

public interface ISelectPreferenceView {
    interface Listener {
        void onAddPreferenceList(List preferenceList) throws JSONException, ParseException;
    }

    View getRootView();
}
