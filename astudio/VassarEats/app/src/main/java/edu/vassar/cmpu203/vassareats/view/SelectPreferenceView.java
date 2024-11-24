package edu.vassar.cmpu203.vassareats.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.vassar.cmpu203.vassareats.MainActivity;
import edu.vassar.cmpu203.vassareats.databinding.ActivityMainBinding;

public class SelectPreferenceView implements ISelectPreferenceView{

    ActivityMainBinding binding;
    Listener listener;
    boolean[] selectedPreference;
    List<Integer> preferenceList = new ArrayList<Integer>();
    String[] preferenceArray = {"Vegetarian", "Vegan", "Halal", "Kosher", "Made without gluten-containing ingredients",
            "Humane", "Farm to Fork"};

    public SelectPreferenceView(Context context, Listener listener) {
        this.listener = listener;
        //    Initialize selected preference array
        selectedPreference = new boolean[preferenceArray.length];

        this.binding = ActivityMainBinding.inflate(LayoutInflater.from(context));
        this.binding.preference.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        context
                );
//                Set title
                builder.setTitle("Select Preference");
//                Set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(preferenceArray, selectedPreference, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                        Check condition
                        if (isChecked) {
//                            When checkbox is selected, add position in preferenceList
                            preferenceList.add(which);
//                            Sort preferenceList
                            Collections.sort(preferenceList);
                        } else {
//                            When checkbox is unselected, remove position from preferenceList
                            preferenceList.remove(which);
                        }
                    }
                });

                builder.setPositiveButton("Apply", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Initialize string builder
                        StringBuilder stringBuilder = new StringBuilder();
//                        Use for loop
                        for (int i = 0; i < preferenceList.size(); i++) {
//                            Concatenate array value
                            stringBuilder.append(preferenceArray[preferenceList.get(i)]);
//                            Check condition
                            if (i != preferenceList.size() - 1) {
//                                When we've not gotten to the end of the list, add a comma
                                stringBuilder.append(", ");
                            }
                        }
//                        Set the preference list to listener
                        try {
                            listener.onAddPreferenceList(preferenceList);
                        } catch (JSONException | ParseException e) {
                            throw new RuntimeException(e);
                        }

//                        Set text on view
                        binding.preference.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Dismiss dialog
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Use for loop
                        for (int i = 0; i < selectedPreference.length; i++) {
//                            Remove all selection
                            selectedPreference[i] = false;
//                            Clear preferenceList
                            preferenceList.clear();
//                            Clear preference value
                            binding.preference.setText("");
                        }
                        try {
                            listener.onAddPreferenceList(preferenceList);
                        } catch (JSONException | ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
//                Show dialog
                builder.show();
            }
        });
    }

    @Override
    public View getRootView() {
        return this.binding.getRoot();
    }
}
