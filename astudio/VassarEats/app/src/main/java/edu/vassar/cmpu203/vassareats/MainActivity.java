package edu.vassar.cmpu203.vassareats;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.vassar.cmpu203.vassareats.model.MealTime;
import edu.vassar.cmpu203.vassareats.model.MealType;
import edu.vassar.cmpu203.vassareats.model.Menu;
import edu.vassar.cmpu203.vassareats.model.Preference;
import edu.vassar.cmpu203.vassareats.view.FoodMenuActivity;
import edu.vassar.cmpu203.vassareats.view.IMenuView;
import edu.vassar.cmpu203.vassareats.view.MealTimeAdapter;
import edu.vassar.cmpu203.vassareats.view.MenuView;

public class MainActivity extends AppCompatActivity implements IMenuView.Listener, MealTimeAdapter.Listener {

    private IMenuView menuView;
    private Menu menu;

    private GestureDetector gestureDetector;
    private MealTimeAdapter mealTimeAdapter; // keep a reference so we can refresh

    // The constructor is not needed for an Activity, it's safer to remove it.
    // public MainActivity() throws JSONException, ParseException {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this); // Can be removed if not being used extensively.

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Initialize menu and views
        try {
            this.menu = new Menu();
        } catch (ParseException | JSONException e) {
            // It's better to log the error and show a message than to crash.
            Log.e("MainActivity", "Failed to initialize Menu model", e);
            // In a real app, you might show an error screen here.
            return; // Exit onCreate if menu fails to load.
        }

        this.menuView = new MenuView(this, this);
        setContentView(menuView.getRootView());

        RecyclerView mealTimeRecyclerView = findViewById(R.id.mealTimeRecyclerView);
        mealTimeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create the data for the meal cards using filtered meal types
        List<MealTime> mealTimes = generateMealTimeData();

        // Create and set the new adapter (store as field so we can update later)
        this.mealTimeAdapter = new MealTimeAdapter(mealTimes, this);
        mealTimeRecyclerView.setAdapter(mealTimeAdapter);

        // Initialize the GestureDetector
        this.gestureDetector = new GestureDetector(this, new OnSwipeGestureListener());

        // Attach the touch listener to the root view of your layout.
        View rootView = menuView.getRootView();
        rootView.setOnTouchListener((v, event) -> {
            // Pass the event to the gesture detector
            boolean handled = gestureDetector.onTouchEvent(event);
            // We also need to allow clicks to work, so performClick on ACTION_UP.
            if (!handled && event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
            }
            return true; // Return true to indicate we've handled the touch event.
        });
    }

    /**
     * This is the callback from the MealTimeAdapter.Listener.
     * It's called when a "View Menu" button is clicked.
     */
    @Override
    public void onMealTimeClick(MealTime mealTime) {
        Intent intent = new Intent(this, FoodMenuActivity.class);

        // Pass the name of the meal (e.g., "Breakfast") to the new activity.
        intent.putExtra(FoodMenuActivity.EXTRA_MEAL_NAME, mealTime.getMealName());

        // We also need to pass the currently selected date and location!
        intent.putExtra("SELECTED_DATE", menu.getCurrentDate().toString());
        intent.putExtra("SELECTED_LOCATION", menu.getCurrentLocation());

        startActivity(intent);
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

    private List<MealTime> generateMealTimeData() {
        List<MealTime> list = new ArrayList<>();
        // FIX: Use getAvailableMealNames to get meal types for the specific location/day
        ArrayList<String> availableMealNames = menu.getAvailableMealNames();
        HashMap<String, Integer> bgMap = mealTimeBgMap();

        for (String mealName : availableMealNames) {
            // You can enhance this to get specific times if your model supports it
            String time = "View Menu";
            Integer bg = bgMap.get(mealName);
            int drawableId = (bg != null) ? bg : R.drawable.breakfast_bg; // Default background
            list.add(new MealTime(mealName, time, drawableId));
        }
        return list;
    }

    // A central method to refresh the meal cards RecyclerView
    private void refreshMealTimeView() {
        if (mealTimeAdapter != null) {
            List<MealTime> newMealTimes = generateMealTimeData();
            mealTimeAdapter.setMealTimes(newMealTimes); // This should call notifyDataSetChanged() inside the adapter
        }
    }

    @Override
    public void updatePreferences(List<Preference.Preferences> preferenceList) {
        menu.changePreferences(preferenceList);
        refreshMealTimeView();
    }

    @Override
    public void updateDate(LocalDate date) throws JSONException, ParseException {
        menu.updateDate(date);
        refreshMealTimeView();
    }



    @Override
    public void updateLocation(Integer diningLocation) throws JSONException, ParseException {
        menu.updateLocation(diningLocation);
        refreshMealTimeView();
    }

    @Override
    public void onHomeIconClick() {
        menu.resetFilters();
        if (menuView instanceof MenuView) {
            ((MenuView) menuView).resetFilters();
        }
        refreshMealTimeView();
    }

    /**
     * Inner class to handle swipe gesture detection.
     */
    private class OnSwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null || e2 == null) return false;
            float diffX = e2.getX() - e1.getX();

            // Check for a significant horizontal swipe
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight(); // Previous Day
                } else {
                    onSwipeLeft(); // Next Day
                }
                return true; // The fling was handled
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true; // Necessary to tell the system we want to handle gestures
        }
    }

    /**
     * Handles the logic for a left swipe (Go to next day).
     */
    private void onSwipeLeft() {
        try {
            menu.goToNextDay();
            updateViewAfterDateChange();
            refreshMealTimeView();
        } catch (Exception e) {
            Log.e("MainActivity", "Error on swiping left", e);
        }
    }

    /**
     * Handles the logic for a right swipe (Go to previous day).
     */
    private void onSwipeRight() {
        try {
            menu.goToPreviousDay();
            updateViewAfterDateChange();
            refreshMealTimeView();
        } catch (Exception e) {
            Log.e("MainActivity", "Error on swiping right", e);
        }
    }

    /**
     * A central method to update the view's date text after a swipe.
     */
    private void updateViewAfterDateChange() {
        if (menuView instanceof MenuView) {
            ((MenuView) menuView).updateDateDisplay(menu.getCurrentDate());
        }
    }
}
