package edu.vassar.cmpu203.vassareats;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.content.Intent;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;

import androidx.annotation.NonNull;
import org.json.JSONException;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.vassar.cmpu203.vassareats.model.MealType;
import edu.vassar.cmpu203.vassareats.model.Menu;
import edu.vassar.cmpu203.vassareats.view.IExpandableRecylerViewAdapter;
import edu.vassar.cmpu203.vassareats.view.ExpandableRecyclerViewAdapter;
import edu.vassar.cmpu203.vassareats.model.Preference;
import edu.vassar.cmpu203.vassareats.view.IMenuView;
import edu.vassar.cmpu203.vassareats.view.MenuView;
import edu.vassar.cmpu203.vassareats.view.MealTimeAdapter;
import edu.vassar.cmpu203.vassareats.view.FoodMenuActivity;
import edu.vassar.cmpu203.vassareats.model.MealTime;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements IMenuView.Listener, MealTimeAdapter.Listener {
//    Initialize variable
    public IMenuView menuView;
    Menu menu;

    private GestureDetector gestureDetector;
    private MealTimeAdapter mealTimeAdapter; // keep a reference so we can refresh

    public MainActivity() throws JSONException, ParseException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Initialize menu and views
        try {
            menu = new Menu();
        } catch (ParseException | JSONException e) {
            throw new RuntimeException(e);
        }

        menuView = new MenuView(this, this);
        setContentView(menuView.getRootView());

        RecyclerView mealTimeRecyclerView = findViewById(R.id.mealTimeRecyclerView);
        mealTimeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create the data for the meal cards using filtered meal types
        List<MealTime> mealTimes = generateMealTimeData();

        // Create and set the new adapter (store as field so we can update later)
        mealTimeAdapter = new MealTimeAdapter(mealTimes, this);
        mealTimeRecyclerView.setAdapter(mealTimeAdapter);

        // Initialize the GestureDetector
        gestureDetector = new GestureDetector(this, new OnSwipeGestureListener());

        // Attach the detector to the RecyclerView's touch events
        menuView.getRootView().setOnTouchListener(new View.OnTouchListener() {
        menuView.getRootView().setOnTouchListener((v, event) -> {
            boolean handled = gestureDetector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
        });
            return handled;
    }


//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        // Convert liked items to a list for serialization
//        outState.putStringArrayList(LIKED_ITEMS_KEY, new ArrayList<>(likedItems));
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        SharedPreferences prefs = getSharedPreferences("AppState", MODE_PRIVATE);
//        prefs.edit().putStringSet(LIKED_ITEMS_KEY, likedItems).apply();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        SharedPreferences prefs = getSharedPreferences("AppState", MODE_PRIVATE);
//        likedItems = new HashSet<>(prefs.getStringSet(LIKED_ITEMS_KEY, new HashSet<>()));
//
//        // Notify the adapter of the updated likedItems
//        recylerViewAdapter.setLikedItems(likedItems);
//    }

    /**
     * This is the callback from the new MealTimeAdapter.Listener.
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
        List<MealType> mealTypes = menu.getFilteredMealTypes(); // use filtered list
        HashMap<String, Integer> bgMap = mealTimeBgMap();
        for (MealType mealType : mealTypes) {
            Integer bg = bgMap.get(mealType.getMealTypeName());
            int drawableId = (bg != null) ? bg : R.drawable.breakfast_bg;
            list.add(new MealTime(mealType.getMealTypeName(), mealType.getMealTypeTime(), drawableId));
        }
        return list;
    }

    @Override
    public void updatePreferences(List<Preference.Preferences> preferenceList) {
        menu.changePreferences(preferenceList);
        // Refresh meal time cards to reflect changed filters
        if (mealTimeAdapter != null) {
            mealTimeAdapter.setMealTimes(generateMealTimeData());
        }
    }

    @Override
    public void updateDate(LocalDate date) throws JSONException, ParseException {
        menu.updateDate(date);
        // Refresh meal time cards to reflect new date
        if (mealTimeAdapter != null) {
            mealTimeAdapter.setMealTimes(generateMealTimeData());
        }
//        recylerViewAdapter.setParentItems(menu.getFilteredMenuParentItems());
    }

    @Override
    public void updateLocation(Integer diningLocation) throws JSONException, ParseException {
        menu.updateLocation(diningLocation);
        // Refresh meal time cards to reflect new location
        if (mealTimeAdapter != null) {
            mealTimeAdapter.setMealTimes(generateMealTimeData());
        }
//        recylerViewAdapter.setParentItems(menu.getFilteredMenuParentItems());
    }

    @Override
    public void onHomeIconClick() {
        menu.resetFilters();

        if (menuView instanceof MenuView) {
            ((MenuView) menuView).resetFilters();
        }
        // Refresh displayed mealTimes after resetting filters
        if (mealTimeAdapter != null) {
            mealTimeAdapter.setMealTimes(generateMealTimeData());
        }
    }

    /**
     * Inner class to handle swipe gesture detection.
     */
    private class OnSwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // e1 is the starting touch event, e2 is the ending touch event
            if (e1 == null || e2 == null) return false;

            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            // Check if it's a horizontal swipe
            if (Math.abs(diffX) > Math.abs(diffY)) {
                // Check if the swipe is significant enough
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Right Swipe (Previous Day)
                        onSwipeRight();
                    } else {
                        // Left Swipe (Next Day)
                        onSwipeLeft();
                    }
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                }
            }
            return false;
        }

        // It's good practice to override onDown to return true,
        // which tells the detector you're interested in gesture events.
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    /**
     * Handles the logic for a left swipe (Go to next day).
     */
    private void onSwipeLeft() {
        try {
            menu.goToNextDay(); // Tell the model to update
            updateViewAfterDateChange(); // Refresh the UI
            if (mealTimeAdapter != null) {
                mealTimeAdapter.setMealTimes(generateMealTimeData());
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error on swiping left", e);
        }
        public boolean onDown(@NonNull MotionEvent e) {

    /**
     * Handles the logic for a right swipe (Go to previous day).
     */
    private void onSwipeRight() {
        try {
            menu.goToPreviousDay(); // Tell the model to update
            updateViewAfterDateChange(); // Refresh the UI
            if (mealTimeAdapter != null) {
                mealTimeAdapter.setMealTimes(generateMealTimeData());
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error on swiping right", e);
        }
    }

    /**
     * A central method to update the view after any date change.
     */
    private void updateViewAfterDateChange() {
//        // Refresh the RecyclerView with new data from the model
//        recylerViewAdapter.setParentItems(menu.getFilteredMenuParentItems());

        // Tell the MenuView to update the date text
        if (menuView instanceof MenuView) {
            ((MenuView) menuView).updateDateDisplay(menu.getCurrentDate());
        }
    }
}