package edu.vassar.cmpu203.vassareats.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.vassar.cmpu203.vassareats.R;
import edu.vassar.cmpu203.vassareats.databinding.ActivityMainBinding;
import edu.vassar.cmpu203.vassareats.model.DiningStation;
import edu.vassar.cmpu203.vassareats.model.FoodItem;
import edu.vassar.cmpu203.vassareats.model.MealType;
import edu.vassar.cmpu203.vassareats.model.MealTypeSection;
import edu.vassar.cmpu203.vassareats.model.Preference;

public class SelectPreferenceView implements ISelectPreferenceView{

    ActivityMainBinding binding;
    Listener listener;
    boolean[] selectedPreference;
    boolean[] selectedPreferenceTemp;
    List<Integer> preferenceList = new ArrayList<Integer>();
    String[] preferenceArray;
    List<Preference.Preferences> preferences = new ArrayList<>();
    List<Preference.Preferences> preferencesTemp = new ArrayList<>();

    public SelectPreferenceView(Context context, Listener listener) {
        this.listener = listener;

        //Add the preferences to the preferenceArray
        preferenceArray = new String[Preference.Preferences.values().length];
        int count = 0;

        for (Preference.Preferences preference : Preference.Preferences.values()) {
            preferenceArray[count] = preference.toString();
            count++;
        }


        //    Initialize selected preference array
        selectedPreference = new boolean[preferenceArray.length];
        selectedPreferenceTemp = new boolean[preferenceArray.length];

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
//                            When checkbox is selected, add the enum to the preferences list
                            preferencesTemp.add(Preference.Preferences.getPreference(preferenceArray[which]));
//                            Sort preferenceList
                            Collections.sort(preferenceList);
                        } else {
//                            When checkbox is unselected, remove position from preferenceList
                            preferencesTemp.remove(Preference.Preferences.getPreference(preferenceArray[which]));
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

                        preferences = preferencesTemp;

                        for (int i = 0; i < selectedPreference.length; i++) {
                            selectedPreferenceTemp[i] = selectedPreference[i];
                        }

//                        Set the preference list to listener
                        try {
                            listener.onAddPreferenceList(preferences);
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
                        preferencesTemp.clear();

                        preferencesTemp.addAll(preferences);

                        for (int i = 0; i < selectedPreferenceTemp.length; i++) {
                            selectedPreference[i] = selectedPreferenceTemp[i];
                        }

//                        Dismiss dialog
                        dialog.dismiss();
//                      Doesn't do anything in terms of reversing selected preferences

                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Use for loop
                        for (int i = 0; i < selectedPreference.length; i++) {
//                            Remove all selection
                            selectedPreference[i] = false;
                            selectedPreferenceTemp[i] = false;
                        }

                        preferences.clear();
                        preferencesTemp.clear();

//                            Clear preference value
                        binding.preference.setText("");

                        try {
                            listener.onAddPreferenceList(preferences);
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
    public void updateMenu(List<MealType> mealTypes, List<String> preferencesString) {
        Context context = binding.getRoot().getContext();

// Make the text screen
        LinearLayout mainLayout = binding.getRoot().findViewById(R.id.main);
        LayoutInflater inflater = LayoutInflater.from(context);
//        int indexBeforeExpandableListView = mainLayout.indexOfChild(mainLayout.findViewById(R.id.expanded_menu));

        View selectPreferenceView = binding.preference; // Assuming this is the SelectPreferenceView
        for (int i = mainLayout.getChildCount() - 1; i >= 2; i--) {
            View child = mainLayout.getChildAt(i);
            if (child != selectPreferenceView) {
                mainLayout.removeViewAt(i);
            }
        }

        int mealTypeTextCount = 0;
        int mealTypeSectionTextCount = 0;
        int diningStationTextCount = 0;
        int foodItemTextCount = 0;

        for (MealType mealType : mealTypes) {

            View mealTypeView = inflater.inflate(R.layout.activity_meal_type, null);
            TextView mealTypeTextView = mealTypeView.findViewById(R.id.mealType);

            mealTypeTextView.setText(mealType.getMealTypeName());

            mealTypeTextView.setId(View.generateViewId());

            mealTypeTextCount ++;

            mealTypeTextView.setTag("mealType_" + mealTypeTextCount);


            mainLayout.addView(mealTypeView);

            int mealTypeCount = mainLayout.getChildCount();

            for (MealTypeSection mealTypeSection : mealType.getMealTypeSections()) {

                View mealTypeSectionView = inflater.inflate(R.layout.activity_meal_type_section, null);
                TextView mealTypeSectionTextView = mealTypeSectionView.findViewById(R.id.mealTypeSection);

                mealTypeSectionTextView.setText(mealTypeSection.getMealTypeSectionName());

                mealTypeSectionTextView.setId(View.generateViewId());

                mealTypeSectionTextCount ++;

                mealTypeSectionTextView.setTag("mealTypeSection_" + mealTypeSectionTextCount);

                mainLayout.addView(mealTypeSectionView);

                int mealTypeSectionCount = mainLayout.getChildCount();

                for (DiningStation diningStation : mealTypeSection.getDiningSections()) {

                    int diningStationCount = mainLayout.getChildCount();

                    View diningSectionView = inflater.inflate(R.layout.activity_dining_section, null);
                    TextView diningSectionTextView = diningSectionView.findViewById(R.id.diningStation);

                    diningSectionTextView.setText(diningStation.getDiningSectionName());

                    diningSectionTextView.setId(View.generateViewId());

                    diningSectionTextView.setTag("diningStation_" + diningStationTextCount);

                    for (FoodItem foodItem : diningStation.getFoodItems()) {

                        if (preferencesString.isEmpty()) {
                            View foodItemView = inflater.inflate(R.layout.activity_food_item, null);
                            TextView foodItemTextView = foodItemView.findViewById(R.id.foodItem);

                            foodItemTextView.setText(foodItem.getFoodItemName());

                            foodItemTextView.setId(View.generateViewId());

                            foodItemTextCount ++;

                            foodItemTextView.setTag("foodItem_" + foodItemTextCount);

                            mainLayout.addView(foodItemView);
                        } else {
                            for (String dietLabel : foodItem.getDietLabels()) {
                                if (preferencesString.contains(dietLabel)) {
                                    View foodItemView = inflater.inflate(R.layout.activity_food_item, null);
                                    TextView foodItemTextView = foodItemView.findViewById(R.id.foodItem);

                                    foodItemTextView.setText(foodItem.getFoodItemName());

                                    foodItemTextView.setId(View.generateViewId());

                                    foodItemTextCount ++;

                                    foodItemTextView.setTag("foodItem_" + foodItemTextCount);

                                    mainLayout.addView(foodItemView);

                                    break;
                                }
                            }
                        }
                    }

                    if (diningStationCount != mainLayout.getChildCount()) {
                        mainLayout.addView(diningSectionView, diningStationCount);
                        diningStationTextCount ++;
                    }
                }

                if (mealTypeSectionCount == mainLayout.getChildCount()) {
                    mainLayout.removeViewAt(mealTypeSectionCount - 1);
                    mealTypeSectionTextCount --;
                }
            }

            if (mealTypeCount == mainLayout.getChildCount()) {
                mainLayout.removeViewAt(mealTypeCount - 1);
                mealTypeTextCount --;
            }
        }
    }

    @Override
    public View getRootView() {
        return this.binding.getRoot();
    }
}
