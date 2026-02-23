package edu.vassar.cmpu203.vassareats;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import java.text.ParseException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import edu.vassar.cmpu203.vassareats.model.Menu;
import edu.vassar.cmpu203.vassareats.view.FoodMenuFragment;
import edu.vassar.cmpu203.vassareats.model.ParentItem;
import edu.vassar.cmpu203.vassareats.view.HomeFragment;
import edu.vassar.cmpu203.vassareats.view.LoginActivity;
import edu.vassar.cmpu203.vassareats.view.NavigationDrawer;
import edu.vassar.cmpu203.vassareats.view.ExpandableRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity implements ExpandableRecyclerViewAdapter.Listener {

    private DrawerLayout drawerLayout;
    private Menu menu;
    private MaterialToolbar topAppBar;
    private ActionBarDrawerToggle drawerToggle;

    // Controller-owned state
    private final Set<String> likedItems = new HashSet<>();
    private final Set<String> dislikedItems = new HashSet<>();
    private final Map<String, byte[]> imageBytesMap = new HashMap<>();
    private List<ParentItem> controllerParentItems = new ArrayList<>();

    // Adapter reference (register from fragment or wherever adapter is created)
    private ExpandableRecyclerViewAdapter registeredAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Standard setup
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            this.menu = new Menu();
        } catch (ParseException | JSONException e) {
            Log.e("MainActivity", "Failed to initialize Menu model", e);
            return;
        }

        // --- TOOLBAR AND DRAWER INITIALIZATION ---
        topAppBar = findViewById(R.id.topAppBar);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Set the toolbar as the activity's action bar. This is still crucial.
        setSupportActionBar(topAppBar);

        // Attach a DrawerToggle and always keep the hamburger indicator active.
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, topAppBar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Ensure nav icon always opens the drawer (never act like Up/back).
        topAppBar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Navigation view item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getMenu().resetFilters();
                replaceFragment(new HomeFragment(), false);
                topAppBar.setTitle("Vassar Eats");
            } else if (itemId == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment(), false);
        }

        NavigationDrawer.setHalfWidth(this);
    }

    public void setControllerParentItems(List<ParentItem> parentItems) {
        this.controllerParentItems = (parentItems != null) ? new ArrayList<>(parentItems) : new ArrayList<>();
        pushFlatListToAdapter();
    }

    private void pushFlatListToAdapter() {
        if (registeredAdapter == null || controllerParentItems == null) return;
        List<Object> flat = new ArrayList<>();
        for (ParentItem p : controllerParentItems) {
            flat.add(p);
            if (p.isExpanded()) {
                flat.addAll(p.getChildItems());
            }
        }
        registeredAdapter.setFlatItems(flat);
    }

    @Override
    public void onParentToggle(String parentId) {
        if (parentId == null || controllerParentItems == null) return;
        for (ParentItem p : controllerParentItems) {
            if (parentId.equals(p.getTitle())) {
                p.setExpanded(!p.isExpanded());
                break;
            }
        }
        pushFlatListToAdapter();
    }

    // Provide a way for the fragment (or wherever adapter is created) to register the adapter
    public void registerAdapter(ExpandableRecyclerViewAdapter adapter) {
        this.registeredAdapter = adapter;
        if (registeredAdapter != null) {
            registeredAdapter.setLikedItems(likedItems);
            registeredAdapter.setDislikedItems(dislikedItems);
            // push any already-fetched images
            for (Map.Entry<String, byte[]> e : imageBytesMap.entrySet()) {
                registeredAdapter.setImageBytes(e.getKey(), e.getValue());
            }
        }
        pushFlatListToAdapter();
    }


    // Listener callbacks invoked by the adapter (view) — controller handles state changes
    @Override
    public void onLikeClicked(String foodId) {
        if (foodId == null) return;

        boolean nowLiked = likedItems.contains(foodId) ? false : true;
        if (nowLiked) {
            likedItems.add(foodId);
            // remove dislike if present
            dislikedItems.remove(foodId);
        } else {
            likedItems.remove(foodId);
        }

        // Push updated sets into adapter so it can re-render
        if (registeredAdapter != null) {
            registeredAdapter.setLikedItems(likedItems);
            registeredAdapter.setDislikedItems(dislikedItems);
        }
        // Optionally persist to model/backend here
    }

    @Override
    public void onDislikeClicked(String foodId) {
        if (foodId == null) return;

        boolean nowDisliked = dislikedItems.contains(foodId) ? false : true;
        if (nowDisliked) {
            dislikedItems.add(foodId);
            likedItems.remove(foodId);
        } else {
            dislikedItems.remove(foodId);
        }

        if (registeredAdapter != null) {
            registeredAdapter.setLikedItems(likedItems);
            registeredAdapter.setDislikedItems(dislikedItems);
        }
        // Optionally persist to model/backend here
    }

    // When image bytes are fetched by the controller (e.g., network or cache), push them to the adapter
    public void onImageFetched(String foodId, byte[] imageBytes) {
        if (foodId == null || imageBytes == null) return;
        imageBytesMap.put(foodId, imageBytes);
        if (registeredAdapter != null) {
            registeredAdapter.setImageBytes(foodId, imageBytes);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Let the drawer toggle handle its own menu clicks.
        if (drawerToggle != null && drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void navigateToFoodMenu(String mealName) {
        FoodMenuFragment foodMenuFragment = FoodMenuFragment.newInstance(mealName);
        replaceFragment(foodMenuFragment, true);
        topAppBar.setTitle(mealName);
    }

    private void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.replace(R.id.fragment_container_view, fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    public Menu getMenu() {
        return this.menu;
    }
}




