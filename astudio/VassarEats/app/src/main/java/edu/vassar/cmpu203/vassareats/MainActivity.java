package edu.vassar.cmpu203.vassareats;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;
import android.view.View;
import android.view.MotionEvent;
import android.view.GestureDetector;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import edu.vassar.cmpu203.vassareats.model.Menu;
import edu.vassar.cmpu203.vassareats.view.IExpandableRecylerViewAdapter;
import edu.vassar.cmpu203.vassareats.view.ExpandableRecyclerViewAdapter;
import edu.vassar.cmpu203.vassareats.model.Preference;
import edu.vassar.cmpu203.vassareats.view.IMenuView;
import edu.vassar.cmpu203.vassareats.view.MenuView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements IMenuView.Listener, IExpandableRecylerViewAdapter.Listener {
//    Initialize variable
    public IMenuView menuView;
    Menu menu;
    public IExpandableRecylerViewAdapter recylerViewAdapter;

    private static final String LIKED_ITEMS_KEY = "likedItems";
    private Set<String> likedItems = new HashSet<>();

    private FirebaseFirestore firestore;

    private static final String PREFS_NAME = "AppPreferences";
    private static final String UNIQUE_ID_KEY = "uniqueId";

    FirestoreHelper firestoreHelper;

    private GestureDetector gestureDetector;


    public MainActivity() throws JSONException, ParseException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        firestore = FirebaseFirestore.getInstance();
        firestoreHelper = new FirestoreHelper();

        String userId = getUniqueId(this);

        // Load liked items from Firestore
        firestoreHelper.loadUserLikedItems(userId, new FirestoreHelper.FirestoreCallback() {
            @Override
            public void onSuccess(List<String> likedItemsFromFirestore) {
                if (likedItemsFromFirestore != null) {
                    likedItems = new HashSet<>(likedItemsFromFirestore);
                    Log.d("MainActivity", "Liked items loaded from Firestore: " + likedItems);

                    // Notify adapter of loaded liked items
                    if (recylerViewAdapter != null) {
                        recylerViewAdapter.setLikedItems(likedItems);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("MainActivity", "Failed to load liked items from Firestore", e);
            }
        });

        // Initialize menu and views
        try {
            menu = new Menu();
        } catch (ParseException | JSONException e) {
            throw new RuntimeException(e);
        }

        recylerViewAdapter = new ExpandableRecyclerViewAdapter(this, this, likedItems);
        recylerViewAdapter.setParentItems(menu.getFilteredMenuParentItems());

        menuView = new MenuView(this, this);
        setContentView(menuView.getRootView());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter((RecyclerView.Adapter) recylerViewAdapter);

        // Initialize the GestureDetector
        gestureDetector = new GestureDetector(this, new OnSwipeGestureListener());

        // Attach the detector to the RecyclerView's touch events
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Let the gesture detector handle the touch event
                return gestureDetector.onTouchEvent(event);
            }
        });
    }


    public static String getUniqueId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String uniqueId = sharedPreferences.getString(UNIQUE_ID_KEY, null);

        if (uniqueId == null) {
            // Generate a new UUID
            uniqueId = UUID.randomUUID().toString();
            sharedPreferences.edit().putString(UNIQUE_ID_KEY, uniqueId).apply();
        }

        return uniqueId;
    }

    public void updateLikedItems(String foodId, boolean isLiked) {
        if (isLiked) {
            likedItems.add(foodId);
        } else {
            likedItems.remove(foodId);
        }

        // Save updated likedItems to SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AppState", MODE_PRIVATE);
        prefs.edit().putStringSet(LIKED_ITEMS_KEY, likedItems).apply();

        // Save updated likedItems to Firestore
        String userId = getUniqueId(this);
        firestoreHelper.saveUserLikedItems(this, userId, new ArrayList<>(likedItems));

//        Log.d("MainActivity", "Liked items on save: " + likedItems);
    }

    public void updateLikeCount(String foodId, boolean isLiked) {
        firestoreHelper.updateLikesCount(foodId, isLiked);
    }

    public void getLikeCount(String foodId, FirestoreHelper.FirestoreCallback2 firestoreCallback) {
        firestoreHelper.getLikeCount(foodId, firestoreCallback);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Convert liked items to a list for serialization
        outState.putStringArrayList(LIKED_ITEMS_KEY, new ArrayList<>(likedItems));
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("AppState", MODE_PRIVATE);
        prefs.edit().putStringSet(LIKED_ITEMS_KEY, likedItems).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("AppState", MODE_PRIVATE);
        likedItems = new HashSet<>(prefs.getStringSet(LIKED_ITEMS_KEY, new HashSet<>()));

        // Notify the adapter of the updated likedItems
        recylerViewAdapter.setLikedItems(likedItems);
    }

    @Override
    public void updatePreferences(List<Preference.Preferences> preferenceList) {
        menu.changePreferences(preferenceList);
        recylerViewAdapter.setParentItems(menu.getFilteredMenuParentItems());
    }

    @Override
    public void updateDate(LocalDate date) throws JSONException, ParseException {
        menu.updateDate(date);
        recylerViewAdapter.setParentItems(menu.getFilteredMenuParentItems());
    }

    @Override
    public void updateLocation(Integer diningLocation) throws JSONException, ParseException {
        menu.updateLocation(diningLocation);
        recylerViewAdapter.setParentItems(menu.getFilteredMenuParentItems());
    }

    @Override
    public void onHomeIconClick() {
        menu.resetFilters();

        recylerViewAdapter.setParentItems(menu.getFilteredMenuParentItems());

        if (menuView instanceof MenuView) {
            ((MenuView) menuView).resetFilters();
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
                    return true; // The event is consumed
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
        } catch (Exception e) {
            Log.e("MainActivity", "Error on swiping left", e);
        }
    }

    /**
     * Handles the logic for a right swipe (Go to previous day).
     */
    private void onSwipeRight() {
        try {
            menu.goToPreviousDay(); // Tell the model to update
            updateViewAfterDateChange(); // Refresh the UI
        } catch (Exception e) {
            Log.e("MainActivity", "Error on swiping right", e);
        }
    }

    /**
     * A central method to update the view after any date change.
     */
    private void updateViewAfterDateChange() {
        // Refresh the RecyclerView with new data from the model
        recylerViewAdapter.setParentItems(menu.getFilteredMenuParentItems());

        // Tell the MenuView to update the date text
        if (menuView instanceof MenuView) {
            ((MenuView) menuView).updateDateDisplay(menu.getCurrentDate());
        }
    }
}