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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import edu.vassar.cmpu203.vassareats.R;
import edu.vassar.cmpu203.vassareats.databinding.ActivityMainBinding;
import edu.vassar.cmpu203.vassareats.model.DiningStation;
import edu.vassar.cmpu203.vassareats.model.FoodItem;
import edu.vassar.cmpu203.vassareats.model.MealType;
import edu.vassar.cmpu203.vassareats.model.MealTypeSection;
import edu.vassar.cmpu203.vassareats.model.Preference;

import java.time.LocalDate;

public class MenuView implements IMenuView {

    ActivityMainBinding binding;
    Listener listener;
    boolean[] selectedPreference;
    boolean[] selectedPreferenceTemp;
    String[] preferenceArray;
    List<Preference.Preferences> preferences = new ArrayList<>();
    List<Preference.Preferences> preferencesTemp = new ArrayList<>();

    int dateItem = 0;
    int tempDateItem = dateItem;
    String[] dateList = new String[7];
    List<LocalDate> localDateList = new ArrayList<LocalDate>();

    public MenuView(Context context, Listener listener) {
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

        //Fill in the dates into the datelists

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM");

        // Fill in the first two days
        dateList[0] = "TODAY";
        localDateList.add(currentDate);
        dateList[1] = "TOMORROW";
        localDateList.add(currentDate.plusDays(1));

        for (int i = 2; i < 7; i++) {
            LocalDate date = currentDate.plusDays(i);
            int dayOfMonth = date.getDayOfMonth();
            String abbreviatedMonth = currentDate.format(monthFormatter).toUpperCase();

            dateList[i] = date.getDayOfWeek().toString() + ", " + abbreviatedMonth + " " + dayOfMonth + getOrdinalSuffix(dayOfMonth);
            localDateList.add(date);
        }

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
                        for (int i = 0; i < preferencesTemp.size(); i++) {
//                            Concatenate array value
                            stringBuilder.append(preferencesTemp.get(i).toString());
//                            Check condition
                            if (i != preferencesTemp.size() - 1) {
//                                When we've not gotten to the end of the list, add a comma
                                stringBuilder.append(", ");
                            }
                        }

                        preferences = preferencesTemp;

                        for (int i = 0; i < selectedPreference.length; i++) {
                            selectedPreferenceTemp[i] = selectedPreference[i];
                        }

                        listener.updatePreferences(preferences);


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

                        listener.updatePreferences(preferences);
                    }
                });
//                Show dialog
                builder.show();
            }
        });

        // Logic for the Data UI

        this.binding.date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        context
                );
//                Set title
                builder.setTitle("Select Date");
//                Set dialog non cancelable
                builder.setCancelable(false);

                builder.setSingleChoiceItems(dateList, dateItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tempDateItem = which;
                    }
                });

                builder.setPositiveButton("Apply", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dateItem = tempDateItem;

                        try {
                            listener.updateDate(localDateList.get(dateItem));

                            //                        Set text on view
                            binding.date.setText(dateList[dateItem]);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tempDateItem = dateItem;

//                        Dismiss dialog
                        dialog.dismiss();

                    }
                });

//                Show dialog
                builder.show();
            }
        });
    }

    private static String getOrdinalSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th"; // Special case for 11th, 12th, 13th
        }
        switch (day % 10) {
            case 1: return "st"; // 1st, 21st, etc.
            case 2: return "nd"; // 2nd, 22nd, etc.
            case 3: return "rd"; // 3rd, 23rd, etc.
            default: return "th"; // All other days
        }
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
