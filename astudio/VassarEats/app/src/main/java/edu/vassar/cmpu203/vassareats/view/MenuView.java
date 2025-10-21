package edu.vassar.cmpu203.vassareats.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.app.Activity;
import android.widget.ImageView;

import org.json.JSONException;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.format.TextStyle;
import java.time.LocalDate;
import java.util.Locale;

import edu.vassar.cmpu203.vassareats.databinding.ActivityMainBinding;
import edu.vassar.cmpu203.vassareats.model.Preference;

public class MenuView implements IMenuView {
    
    ActivityMainBinding binding;
    Listener listener;
    private ImageView homeIcon;
    boolean[] selectedPreference;
    boolean[] selectedPreferenceTemp;
    String[] preferenceArray;
    List<Preference.Preferences> preferences = new ArrayList<>();
    List<Preference.Preferences> preferencesTemp = new ArrayList<>();

    int locationItem = 0;
    int tempLocationItem = locationItem;
    String[] locationList = new String[3];

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

        //Fill in the dates into the date list

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

        // Fill in the location days
        locationList[0] = "GORDON COMMONS";
        locationList[1] = "EXPRESS";
        locationList[2] = "STREET EATS";

        this.binding = ActivityMainBinding.inflate(LayoutInflater.from(context));
        this.homeIcon = this.binding.homeIcon;

        // Set the initial default state of the view
        resetFilters();

        // Inform the controller to load the initial data based on these defaults
        try {
            listener.updateLocation(locationItem);
            listener.updateDate(localDateList.get(dateItem));
        } catch (JSONException | ParseException e) {
            throw new RuntimeException("Failed to load initial data on startup", e);
        }


        this.homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    listener.onHomeIconClick();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Logic for the Dining Location UI

        this.binding.diningLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        context
                );
//                Set title
                builder.setTitle("Select Dining Location");
//                Set dialog non cancelable
                builder.setCancelable(false);

                builder.setSingleChoiceItems(locationList, locationItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tempLocationItem = which;
                    }
                });

                builder.setPositiveButton("Apply", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        locationItem = tempLocationItem;

                        try {
                            listener.updateLocation(locationItem);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                        //                        Set text on view
                        binding.diningLocation.setText(locationList[locationItem]);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tempLocationItem = locationItem;

//                        Dismiss dialog
                        dialog.dismiss();

                    }
                });

//                Show dialog
                builder.show();
            }
        });

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

    public void resetFilters() {
        // Reset preferences
        preferences.clear();
        preferencesTemp.clear();
        for (int i = 0; i < selectedPreference.length; i++) {
            selectedPreference[i] = false;
            selectedPreferenceTemp[i] = false;
        }
        binding.preference.setText("");

        // Reset location
        locationItem = 0;
        tempLocationItem = locationItem;
        binding.diningLocation.setText(locationList[locationItem]);

        // Reset date
        dateItem = 0;
        tempDateItem = dateItem;
        binding.date.setText(dateList[dateItem]);
    }

    /**
     * Called by the controller to update the date TextView
     * after a swipe or other date change event.
     */
    public void updateDateDisplay(LocalDate date) {
        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
        int dayOfMonth = date.getDayOfMonth();

        String dateString = String.format("%s, %s %d%s", dayOfWeek, month, dayOfMonth, getOrdinalSuffix(dayOfMonth));

        // Check if it's today
        if (date.isEqual(LocalDate.now())) {
            dateString = "Today";
        }

        binding.date.setText(dateString);
    }

    @Override
    public View getRootView() {
        return this.binding.getRoot();
    }
}
