package edu.vassar.cmpu203.vassareats.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.vassar.cmpu203.vassareats.MainActivity;
import edu.vassar.cmpu203.vassareats.R;
import edu.vassar.cmpu203.vassareats.model.MealTime;
import edu.vassar.cmpu203.vassareats.model.Menu;
import edu.vassar.cmpu203.vassareats.model.Preference;


public class HomeFragment extends Fragment implements IMenuView.Listener, MealTimeAdapter.Listener {

    private static final String PREFS_NAME = "VassarEats_Prefs";
    private static final String KEY_SAVED_PREFS = "saved_preferences_set";

    private IMenuView menuView;
    private Menu menu; // A reference to the central model in MainActivity
    private GestureDetector gestureDetector;
    private MealTimeAdapter mealTimeAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get the shared Menu model from the hosting MainActivity
        this.menu = ((MainActivity) requireActivity()).getMenu();

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Then, create the MenuView, passing it the inflated view
        this.menuView = new MenuView(requireContext(), view, this);
        if (this.menuView instanceof MenuView) {
            ((MenuView) this.menuView).syncFromModel(this.menu);
        }

        reapplyPersistedPreferences();

        // Return the view that MenuView now manages
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView mealTimeRecyclerView = view.findViewById(R.id.mealTimeRecyclerView);
        mealTimeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        this.mealTimeAdapter = new MealTimeAdapter(new ArrayList<>(), this);
        mealTimeRecyclerView.setAdapter(mealTimeAdapter);

        // Initial UI update
        updateMealCardDisplay();

        // Set up swipe gestures
        this.gestureDetector = new GestureDetector(requireContext(), new OnSwipeGestureListener());
        view.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
    }

    @Override
    public void onMealTimeClick(MealTime mealTime) {
        // Instead of starting an activity, tell MainActivity to navigate
        ((MainActivity) requireActivity()).navigateToFoodMenu(mealTime.getMealName());
    }

    // All the logic for generating data and handling UI updates now lives here

    private List<MealTime> generateMealTimeData() {
        List<MealTime> list = new ArrayList<>();
        ArrayList<String> availableMealNames = menu.getAvailableMealNames();
        HashMap<String, Integer> bgMap = mealTimeBgMap();
        for (String mealName : availableMealNames) {
            String time = "View Menu"; // Placeholder
            Integer bg = bgMap.get(mealName);
            int drawableId = (bg != null) ? bg : R.drawable.breakfast_bg;
            list.add(new MealTime(mealName, time, drawableId));
        }
        return list;
    }

    private void updateMealCardDisplay() {
        View view = getView();
        if (view == null) return;

        RecyclerView recyclerView = view.findViewById(R.id.mealTimeRecyclerView);
        TextView noMenuTextView = view.findViewById(R.id.noMenuTextView);

        List<MealTime> mealTimes = generateMealTimeData();
        if (mealTimes.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noMenuTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noMenuTextView.setVisibility(View.GONE);
        }
        if (mealTimeAdapter != null) {
            mealTimeAdapter.setMealTimes(mealTimes);
        }
    }

    // IMenuView.Listener implementations
    @Override
    public void updatePreferences(List<Preference.Preferences> preferenceList) {
        menu.changePreferences(preferenceList);
        persistPreferencesFromModel();
        updateMealCardDisplay();
    }

    @Override
    public void updateDate(LocalDate date) throws JSONException, ParseException {
        menu.updateDate(date);
        updateMealCardDisplay();
    }

    @Override
    public void updateLocation(Integer diningLocation) throws JSONException, ParseException {
        menu.updateLocation(diningLocation);
        updateMealCardDisplay();
    }

    @Override
    public void onHomeIconClick() {
        try {
            // Persist current model preferences immediately (ensures saved if any reset happens)
            persistPreferencesFromModel();

            // Re-apply persisted prefs to the model (covers cases where reset happens elsewhere)
            reapplyPersistedPreferences();

            // Re-sync MenuView UI from the model so dialogs reflect the model
            if (this.menuView instanceof MenuView) {
                ((MenuView) this.menuView).syncFromModel(this.menu);
            }

            // 4) Refresh the meal card list
            updateMealCardDisplay();
        } catch (Exception e) {
            Log.e("HomeFragment", "Error preserving preferences when Home clicked", e);
            // Fallback: re-sync view from model
            if (this.menuView instanceof MenuView) {
                ((MenuView) this.menuView).syncFromModel(this.menu);
            }
            updateMealCardDisplay();
        }
    }

    private void persistPreferencesFromModel() {
        try {
            List<String> prefStrings = menu.getPreferences();
            if (prefStrings == null) prefStrings = new ArrayList<>();

            SharedPreferences sp = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            Set<String> set = new HashSet<>(prefStrings);
            editor.putStringSet(KEY_SAVED_PREFS, set);
            editor.apply();
        } catch (Exception e) {
            Log.w("HomeFragment", "Failed to persist preferences", e);
        }
    }

    // Load persisted preference strings and apply them to the Menu model
    private void reapplyPersistedPreferences() {
        try {
            SharedPreferences sp = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            Set<String> set = sp.getStringSet(KEY_SAVED_PREFS, null);
            if (set == null || set.isEmpty()) return;

            List<Preference.Preferences> savedPrefs = new ArrayList<>();
            for (String s : set) {
                if (s == null) continue;
                Preference.Preferences p = null;
                try { p = Preference.Preferences.valueOf(s); } catch (IllegalArgumentException ignored) {}
                if (p == null) {
                    try { p = Preference.Preferences.getPreference(s); } catch (Exception ignored) {}
                }
                if (p == null) {
                    for (Preference.Preferences cand : Preference.Preferences.values()) {
                        if (cand.toString().equalsIgnoreCase(s)) {
                            p = cand; break;
                        }
                    }
                }
                if (p != null) savedPrefs.add(p);
            }

            if (!savedPrefs.isEmpty()) {
                menu.changePreferences(savedPrefs);
            }
        } catch (Exception e) {
            Log.w("HomeFragment", "Failed to reapply persisted preferences", e);
        }
    }

    // Swipe Gesture Handling
    private class OnSwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null || e2 == null) return false;
            if (Math.abs(e1.getX() - e2.getX()) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (e1.getX() > e2.getX()) onSwipeLeft(); else onSwipeRight();
                return true;
            }
            return false;
        }
        @Override public boolean onDown(MotionEvent e) { return true; }
    }

    private void onSwipeLeft() {
        try {
            menu.goToNextDay();
            updateViewAfterDateChange();
            updateMealCardDisplay();
        } catch (Exception e) { Log.e("HomeFragment", "Error on swipe left", e); }
    }

    private void onSwipeRight() {
        try {
            menu.goToPreviousDay();
            updateViewAfterDateChange();
            updateMealCardDisplay();
        } catch (Exception e) { Log.e("HomeFragment", "Error on swipe right", e); }
    }

    private void updateViewAfterDateChange() {
        if (menuView instanceof MenuView) {
            ((MenuView) menuView).updateDateDisplay(menu.getCurrentDate());
        }
    }

    private HashMap<String, Integer> mealTimeBgMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Breakfast", R.drawable.breakfast_bg);
        map.put("Brunch", R.drawable.brunch_bg);
        map.put("Light Lunch", R.drawable.light_lunch_bg);
        map.put("Lunch", R.drawable.lunch_bg);
        map.put("Dinner", R.drawable.dinner_bg);
        map.put("Late Night", R.drawable.late_night_bg);
        map.put("Express", R.drawable.express_bg);
        return map;
    }

    /* Ensure the UI is re-synced when fragment resumes (covers cases where view was recreated) */
    @Override
    public void onResume() {
        super.onResume();
        if (this.menuView instanceof MenuView) {
            ((MenuView) this.menuView).syncFromModel(this.menu);
        }
    }
}

