package edu.vassar.cmpu203.vassareats.view;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.List;

import edu.vassar.cmpu203.vassareats.R;
import edu.vassar.cmpu203.vassareats.FirestoreHelper;
import edu.vassar.cmpu203.vassareats.model.Menu;

public class FoodMenuActivity extends AppCompatActivity implements IExpandableRecylerViewAdapter.Listener {

    public static final String EXTRA_MEAL_NAME = "MEAL_NAME";

    // menu removed as a field to reduce warning; used locally in onCreate
    private ExpandableRecyclerViewAdapter adapter;
    private FirestoreHelper firestoreHelper;
    private Set<String> likedItems = new HashSet<>();
    private static final String LIKED_ITEMS_KEY = "likedItemsState";
    private String userId;
    private static final String PREFS_NAME = "AppPreferences";
    private static final String UNIQUE_ID_KEY = "uniqueId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);

        firestoreHelper = new FirestoreHelper();

        userId = getUniqueId(this);

        // Check if we are restoring from a saved state (like screen rotation)
        if (savedInstanceState != null) {
            // If so, restore the liked items directly from the bundle. It's faster.
            ArrayList<String> savedLikes = savedInstanceState.getStringArrayList(LIKED_ITEMS_KEY);
            if (savedLikes != null) {
                this.likedItems = new HashSet<>(savedLikes);
                Log.d("FoodMenuActivity", "Restored liked items from savedInstanceState.");
            }
        }

        // Load from Firestore ONLY if we didn't restore from a bundle.
        // The `likedItems.isEmpty()` check prevents re-loading on screen rotation.
        if (likedItems.isEmpty()) {
            firestoreHelper.loadUserLikedItems(userId, new FirestoreHelper.FirestoreCallback() {
                @Override
                public void onSuccess(List<String> likedItemsFromFirestore) {
                    if (likedItemsFromFirestore != null) {
                        likedItems.addAll(likedItemsFromFirestore);
                        Log.d("FoodMenuActivity", "Loaded liked items from Firestore.");
                        if (adapter != null) {
                            adapter.setLikedItems(likedItems);
                        }
                    }
                }
                @Override
                public void onFailure(Exception e) {
                    Log.e("FoodMenuActivity", "Failed to load liked items from Firestore", e);
                }
            });
        }

        // 1. Get the meal name passed from MainActivity
        String mealName = getIntent().getStringExtra(EXTRA_MEAL_NAME);
        String dateString = getIntent().getStringExtra("SELECTED_DATE");
        int location = getIntent().getIntExtra("SELECTED_LOCATION", 0); // Default to location 0
        if (mealName == null) {
            Log.e("FoodMenuActivity", "Meal name not provided!");
            finish(); // Close the activity if no data is provided
            return;
        }
        LocalDate selectedDate = java.time.LocalDate.parse(dateString);

        // 2. Set up the Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(mealName + " Menu");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 3. Initialize the menu model and filter it
        try {
            // Make menu a local variable
            Menu menu = new Menu();
            menu.updateDate(selectedDate);       // Apply the date from MainActivity
            menu.updateLocation(location);     // Apply the location from MainActivity

            // Validate that the requested mealName exists in the filtered menu for this date/location.
            List<edu.vassar.cmpu203.vassareats.model.MealType> available = menu.getFilteredMealTypes();
            boolean found = false;
            if (available != null) {
                for (edu.vassar.cmpu203.vassareats.model.MealType mt : available) {
                    if (mt.getMealTypeName().equalsIgnoreCase(mealName)) {
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                if (available == null || available.isEmpty()) {
                    Log.e("FoodMenuActivity", "No meal types available for selected location/date.");
                    Toast.makeText(this, "No meals available for the selected location/date.", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                } else {
                    // pick the first available meal type and notify the user
                    String newMeal = available.get(0).getMealTypeName();
                    Toast.makeText(this, "Requested meal not available. Showing " + newMeal + " instead.", Toast.LENGTH_LONG).show();
                    mealName = newMeal;
                    toolbar.setTitle(mealName + " Menu");
                    Log.w("FoodMenuActivity", "Requested meal type not available; switching to " + mealName);
                }
            }

            ArrayList<ParentItem> filteredItems = menu.getItemsForMealType(mealName);

            // 4. Set up the RecyclerView
            RecyclerView recyclerView = findViewById(R.id.foodMenuRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // We can reuse the existing adapter!
            this.adapter = new ExpandableRecyclerViewAdapter(this, filteredItems, this, likedItems);
            recyclerView.setAdapter(this.adapter);

        } catch (Exception e) {
            Log.e("FoodMenuActivity", "Error initializing menu", e);
            Toast.makeText(this, "Error loading menu.", Toast.LENGTH_SHORT).show();
        }
    }

    // This saves the liked items when the screen rotates.
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Only save if likedItems is not empty.
        if (this.likedItems != null && !this.likedItems.isEmpty()) {
            outState.putStringArrayList(LIKED_ITEMS_KEY, new ArrayList<>(this.likedItems));
            Log.d("FoodMenuActivity", "Saving liked items to outState bundle.");
        }
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

    // This method handles the click on the toolbar's back arrow
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back to the previous screen (MainActivity)
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Required by IExpandableRecylerViewAdapter.Listener
    @Override
    public void onLikeClicked(String foodId, boolean isLiked) {
        // 1. Update the local set for immediate UI feedback (You should add this)
        if (isLiked) {
            likedItems.add(foodId);
        } else {
            likedItems.remove(foodId);
        }

        // 2. Save the user's personal list of liked items
        firestoreHelper.saveUserLikedItems(this, userId, new ArrayList<>(likedItems));

        // 3. Update the public likes counter
        long change = isLiked ? 1 : -1;
        firestoreHelper.updateLikesCount(foodId, change, (success, e) -> {
            if (success) {
                Log.d("FoodMenuActivity", "Likes count updated successfully for " + foodId);
            } else {
                Log.e("FoodMenuActivity", "Failed to update likes count for " + foodId, e);
            }
        });
    }
}
