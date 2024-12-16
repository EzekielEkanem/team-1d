package edu.vassar.cmpu203.vassareats;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import edu.vassar.cmpu203.vassareats.model.FoodItem;
import edu.vassar.cmpu203.vassareats.model.Menu;
import edu.vassar.cmpu203.vassareats.view.IExpandableRecylerViewAdapter;
import edu.vassar.cmpu203.vassareats.view.ParentItem;
import edu.vassar.cmpu203.vassareats.view.ExpandableRecyclerViewAdapter;
import edu.vassar.cmpu203.vassareats.model.Preference;
import edu.vassar.cmpu203.vassareats.view.IMenuView;
import edu.vassar.cmpu203.vassareats.view.MenuView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Context;
import android.content.SharedPreferences;

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

        menuView = new MenuView(this, this);
        setContentView(menuView.getRootView());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recylerViewAdapter = new ExpandableRecyclerViewAdapter(this, this, likedItems);
        recylerViewAdapter.setParentItems(menu.getFilteredMenuParentItems());
        recyclerView.setAdapter((RecyclerView.Adapter) recylerViewAdapter);
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
}