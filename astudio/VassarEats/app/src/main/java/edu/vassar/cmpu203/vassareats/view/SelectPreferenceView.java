package edu.vassar.cmpu203.vassareats.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.vassar.cmpu203.vassareats.MainActivity;
import edu.vassar.cmpu203.vassareats.R;
import edu.vassar.cmpu203.vassareats.databinding.ActivityMainBinding;
import edu.vassar.cmpu203.vassareats.model.DiningSection;
import edu.vassar.cmpu203.vassareats.model.FoodItem;
import edu.vassar.cmpu203.vassareats.model.MealType;
import edu.vassar.cmpu203.vassareats.model.MealTypeSection;

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
//                        Log.e("Testing", "Which: " + which + " isChecked: " + isChecked);
//                        Check condition
                        if (isChecked) {
//                            When checkbox is selected, add position in preferenceList
                            preferenceList.add(which);
//                            Sort preferenceList
                            Collections.sort(preferenceList);
                        } else {
//                            When checkbox is unselected, remove position from preferenceList
                            for (int i = 0; i < preferenceList.size(); i++) {
                                if (preferenceList.get(i) == which) {
                                    preferenceList.remove(i);
                                }
                            }
//                            preferenceList.remove(which);
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

                        //Doesn't do anything in terms of reversing selected preferences
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Use for loop
                        for (int i = 0; i < selectedPreference.length; i++) {
//                            Remove all selection
                            selectedPreference[i] = false;
                        }
                        // Clear preferenceList
                        preferenceList.clear();

//                        Clear preference value
                        binding.preference.setText("");

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
    public void updateMenu(List<MealType> mealTypes) {
//        Log.e("Testing", "is this function running");
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

        for (MealType mealType : mealTypes) {

            View mealTypeView = inflater.inflate(R.layout.activity_meal_type, null);
            TextView mealTypeTextView = mealTypeView.findViewById(R.id.mealType);

            mealTypeTextView.setText(mealType.getMealTypeName());

            mainLayout.addView(mealTypeView);

            for (MealTypeSection mealTypeSection : mealType.getMealTypeSections()) {

                View mealTypeSectionView = inflater.inflate(R.layout.activity_meal_type_section, null);
                TextView mealTypeSectionTextView = mealTypeSectionView.findViewById(R.id.mealTypeSection);

                mealTypeSectionTextView.setText(mealTypeSection.getMealTypeSectionName());

                mainLayout.addView(mealTypeSectionView);

                for (DiningSection diningSection : mealTypeSection.getDiningSections()) {

                    View diningSectionView = inflater.inflate(R.layout.activity_dining_section, null);
                    TextView diningSectionTextView = diningSectionView.findViewById(R.id.diningStation);

                    diningSectionTextView.setText(diningSection.getDiningSectionName());

                    mainLayout.addView(diningSectionView);

                    for (FoodItem foodItem : diningSection.getFoodItems()) {

                        View foodItemView = inflater.inflate(R.layout.activity_food_item, null);
                        TextView foodItemTextView = foodItemView.findViewById(R.id.foodItem);

                        foodItemTextView.setText(foodItem.getFoodItemName());

                        mainLayout.addView(foodItemView);

                    }
                }
            }
        }
    }

    @Override
    public View getRootView() {
        return this.binding.getRoot();
    }
}
