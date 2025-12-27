package edu.vassar.cmpu203.vassareats.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.TextView; // Import TextView

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import edu.vassar.cmpu203.vassareats.R; // Import R
import edu.vassar.cmpu203.vassareats.model.Preference;
import edu.vassar.cmpu203.vassareats.model.Menu;

public class MenuView implements IMenuView {

    private final View rootView; // This will be the fragment's view
    private final Listener listener;

    // References to the UI widgets
    private final TextView diningLocationTextView;
    private final TextView preferenceTextView;
    private final TextView dateTextView;

    // State for the preference dialog
    private final boolean[] selectedPreference;
    private final boolean[] selectedPreferenceTemp;
    private final String[] preferenceArray;
    private final List<Preference.Preferences> preferences = new ArrayList<>();
    private final List<Preference.Preferences> preferencesTemp = new ArrayList<>();

    // State for the location dialog
    private int locationItem = 0;
    private int tempLocationItem = 0;
    private final String[] locationList = new String[4];

    // State for the date dialog
    private int dateItem = 0;
    private int tempDateItem = 0;
    private final String[] dateList = new String[7];
    private final List<LocalDate> localDateList = new ArrayList<>();

    /**
     * Constructor for the new fragment-based architecture.
     * It takes the inflated view from HomeFragment and finds the widgets within it.
     *
     * @param context The context, usually from requireContext().
     * @param view The root view of the fragment (from onCreateView).
     * @param listener The listener, usually the fragment itself.
     */
    public MenuView(Context context, View view, Listener listener) {
        this.rootView = view;
        this.listener = listener;

        // Find the views within the provided fragment layout
        this.diningLocationTextView = view.findViewById(R.id.diningLocation);
        this.preferenceTextView = view.findViewById(R.id.preference);
        this.dateTextView = view.findViewById(R.id.date);

        // --- ALL DIALOG AND STATE INITIALIZATION LOGIC REMAINS THE SAME ---

        //Add the preferences to the preferenceArray
        preferenceArray = new String[Preference.Preferences.values().length];
        int count = 0;
        for (Preference.Preferences preference : Preference.Preferences.values()) {
            preferenceArray[count] = preference.toString();
            count++;
        }

        // Initialize selected preference arrays
        selectedPreference = new boolean[preferenceArray.length];
        selectedPreferenceTemp = new boolean[preferenceArray.length];

        // Fill in the dates into the date list
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM");
        dateList[0] = "TODAY";
        localDateList.add(currentDate);
        dateList[1] = "TOMORROW";
        localDateList.add(currentDate.plusDays(1));
        for (int i = 2; i < 7; i++) {
            LocalDate date = currentDate.plusDays(i);
            int dayOfMonth = date.getDayOfMonth();
            String abbreviatedMonth = date.format(monthFormatter).toUpperCase(); // Use the correct date
            dateList[i] = date.getDayOfWeek().toString() + ", " + abbreviatedMonth + " " + dayOfMonth + getOrdinalSuffix(dayOfMonth);
            localDateList.add(date);
        }

        // Fill in the location list
        locationList[0] = "GORDON COMMONS";
        locationList[1] = "EXPRESS";
        locationList[2] = "STREET EATS";
        locationList[3] = "THE RETREAT";

        // Set initial text
        this.diningLocationTextView.setText(locationList[0]);
        this.dateTextView.setText(dateList[0]);
        this.preferenceTextView.setHint("Select Preference");

        // --- CLICK LISTENER LOGIC ---
        // Logic for the Dining Location UI
        this.diningLocationTextView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Select Dining Location");
            builder.setCancelable(false);

            builder.setSingleChoiceItems(locationList, locationItem, (dialog, which) -> tempLocationItem = which);

            builder.setPositiveButton("Apply", (dialog, which) -> {
                locationItem = tempLocationItem;
                try {
                    listener.updateLocation(locationItem);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                diningLocationTextView.setText(locationList[locationItem]);
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> {
                tempLocationItem = locationItem;
                dialog.dismiss();
            });
            builder.show();
        });

        // Logic for the Preference UI
        this.preferenceTextView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Select Preference");
            builder.setCancelable(false);

            builder.setMultiChoiceItems(preferenceArray, selectedPreference, (dialog, which, isChecked) -> {
                if (isChecked) {
                    preferencesTemp.add(Preference.Preferences.getPreference(preferenceArray[which]));
                } else {
                    preferencesTemp.remove(Preference.Preferences.getPreference(preferenceArray[which]));
                }
            });

            builder.setPositiveButton("Apply", (dialog, which) -> {
                StringBuilder stringBuilder = new StringBuilder();
                preferences.clear();
                preferences.addAll(preferencesTemp);
                System.arraycopy(selectedPreference, 0, selectedPreferenceTemp, 0, selectedPreference.length);

                for (int i = 0; i < preferences.size(); i++) {
                    stringBuilder.append(preferences.get(i).toString());
                    if (i != preferences.size() - 1) {
                        stringBuilder.append(", ");
                    }
                }
                listener.updatePreferences(preferences);
                preferenceTextView.setText(stringBuilder.toString());
                if (preferences.isEmpty()) {
                    preferenceTextView.setText("");
                    preferenceTextView.setHint("Select Preference");
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> {
                preferencesTemp.clear();
                preferencesTemp.addAll(preferences);
                System.arraycopy(selectedPreferenceTemp, 0, selectedPreference, 0, selectedPreference.length);
                dialog.dismiss();
            });

            builder.setNeutralButton("Clear All", (dialog, which) -> {
                for (int i = 0; i < selectedPreference.length; i++) {
                    selectedPreference[i] = false;
                    selectedPreferenceTemp[i] = false;
                }
                preferences.clear();
                preferencesTemp.clear();
                preferenceTextView.setText("");
                preferenceTextView.setHint("Select Preference");
                listener.updatePreferences(preferences);
            });
            builder.show();
        });

        // Logic for the Date UI
        this.dateTextView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Select Date");
            builder.setCancelable(false);
            builder.setSingleChoiceItems(dateList, dateItem, (dialog, which) -> tempDateItem = which);
            builder.setPositiveButton("Apply", (dialog, which) -> {
                dateItem = tempDateItem;
                try {
                    listener.updateDate(localDateList.get(dateItem));
                    dateTextView.setText(dateList[dateItem]);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                tempDateItem = dateItem;
                dialog.dismiss();
            });
            builder.show();
        });
    }

    private static String getOrdinalSuffix(int day) {
        if (day >= 11 && day <= 13) return "th";
        switch (day % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }

    public void updateDateDisplay(LocalDate date) {
        // Find the index of the date in our list to keep the dialog selection in sync
        int index = localDateList.indexOf(date);
        if (index != -1) {
            dateItem = index;
            tempDateItem = index;
            dateTextView.setText(dateList[index]);
        } else {
            // If the date is not in our pre-defined list, just format it
            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
            dateTextView.setText(date.format(formatter));
        }
    }

    public void resetFilters() {
        this.locationItem = 0;
        this.tempLocationItem = 0;
        this.diningLocationTextView.setText(this.locationList[0]);

        this.dateItem = 0;
        this.tempDateItem = 0;
        this.dateTextView.setText(this.dateList[0]);

        this.preferences.clear();
        this.preferencesTemp.clear();
        for (int i = 0; i < this.selectedPreference.length; i++) {
            this.selectedPreference[i] = false;
            this.selectedPreferenceTemp[i] = false;
        }
        this.preferenceTextView.setText("");
        this.preferenceTextView.setHint("Select Preference");
    }

    // java
    public void syncFromModel(Menu menu) {
        if (menu == null || this.rootView == null) return;

        this.rootView.post(() -> {
            // 1) Date
            try {
                updateDateDisplay(menu.getCurrentDate());
            } catch (Exception e) {
                Log.w("MenuView", "Failed to set date display from model", e);
            }

            // 2) Location
            try {
                int location = menu.getCurrentLocation();
                if (location >= 0 && location < locationList.length) {
                    this.locationItem = location;
                    this.tempLocationItem = location;
                    this.diningLocationTextView.setText(this.locationList[location]);
                } else {
                    Log.w("MenuView", "Model returned out-of-range location: " + location);
                }
            } catch (Exception e) {
                Log.w("MenuView", "Failed to set location selection from model", e);
            }

            // 3) Preferences -- read strings from Menu and convert to enum list
            try {
                List<String> prefStrings = menu.getPreferences();
                if (prefStrings == null) prefStrings = new java.util.ArrayList<>();

                // Convert strings to Preference.Preferences enums
                this.preferences.clear();
                for (String s : prefStrings) {
                    if (s == null) continue;
                    Preference.Preferences p = null;
                    try {
                        p = Preference.Preferences.valueOf(s);
                    } catch (IllegalArgumentException ignored) {
                        // Try helper if valueOf fails (some projects provide a utility)
                        try {
                            p = Preference.Preferences.getPreference(s);
                        } catch (Exception ignore) { /* skip invalid */ }
                    }
                    if (p != null) this.preferences.add(p);
                }

                // Reset selection arrays
                java.util.Arrays.fill(this.selectedPreference, false);
                java.util.Arrays.fill(this.selectedPreferenceTemp, false);

                // Mark matching indices in the boolean arrays based on preferenceArray
                for (Preference.Preferences p : this.preferences) {
                    if (p == null) continue;
                    String name = p.toString();
                    for (int i = 0; i < this.preferenceArray.length; i++) {
                        if (this.preferenceArray[i].equals(name)) {
                            this.selectedPreference[i] = true;
                            this.selectedPreferenceTemp[i] = true;
                            break;
                        }
                    }
                }

                // Update preference TextView text/hint
                if (this.preferences.isEmpty()) {
                    this.preferenceTextView.setText("");
                    this.preferenceTextView.setHint("Select Preference");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < this.preferences.size(); i++) {
                        sb.append(this.preferences.get(i).toString());
                        if (i != this.preferences.size() - 1) sb.append(", ");
                    }
                    this.preferenceTextView.setText(sb.toString());
                }
            } catch (Exception e) {
                Log.w("MenuView", "Failed to set preferences from model", e);
            }
        });
    }



    @Override
    public View getRootView() {
        return this.rootView;
    }
}

