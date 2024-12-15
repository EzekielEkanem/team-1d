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

public class MainActivity extends AppCompatActivity implements IMenuView.Listener, IExpandableRecylerViewAdapter.Listener {
//    Initialize variable
    public IMenuView menuView;
    Menu menu;
    public IExpandableRecylerViewAdapter recylerViewAdapter;

    private static final String LIKED_ITEMS_KEY = "likedItems";
    private Set<String> likedItems = new HashSet<>();


    public MainActivity() throws JSONException, ParseException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Initialize menu
        try {
            menu = new Menu();
        } catch (ParseException | JSONException e) {
            throw new RuntimeException(e);
        }

        // Restore liked items from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AppState", MODE_PRIVATE);
        likedItems = new HashSet<>(prefs.getStringSet(LIKED_ITEMS_KEY, new HashSet<>()));

        Log.d("MainActivity", "Liked items on load: " + likedItems);

        // Set up menu view and RecyclerView
        this.menuView = new MenuView(this, this);
        setContentView(menuView.getRootView());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with loaded likedItems
        recylerViewAdapter = new ExpandableRecyclerViewAdapter(this, this, likedItems);
        recylerViewAdapter.setParentItems(menu.getFilteredMenuParentItems());
        recyclerView.setAdapter((RecyclerView.Adapter) recylerViewAdapter);
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
        Log.d("MainActivity", "Liked items on save: " + likedItems);
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