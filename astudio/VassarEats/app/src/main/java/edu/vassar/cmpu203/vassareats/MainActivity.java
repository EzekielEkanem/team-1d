package edu.vassar.cmpu203.vassareats;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import java.text.ParseException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import edu.vassar.cmpu203.vassareats.model.FirestoreHelper;
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
    private final Set<String> reportedItems = new HashSet<>();
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

        // --- IMAGE MIGRATION ---
        checkAndMigrateImages();

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

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId != null) {
            FirestoreHelper firestoreHelper = new FirestoreHelper();
            firestoreHelper.loadUserLikedItems(userId, new FirestoreHelper.FirestoreCallback() {
                @Override
                public void onSuccess(List<String> items) {
                    if (items != null) {
                        likedItems.addAll(items);
                        if (registeredAdapter != null) registeredAdapter.setLikedItems(likedItems);
                    }
                }
                @Override
                public void onFailure(Exception e) {
                    Log.e("MainActivity", "Failed to load liked items", e);
                }
            });

            firestoreHelper.loadUserDislikedItems(userId, new FirestoreHelper.FirestoreCallback() {
                @Override
                public void onSuccess(List<String> items) {
                    if (items != null) {
                        dislikedItems.addAll(items);
                        if (registeredAdapter != null) registeredAdapter.setDislikedItems(dislikedItems);
                    }
                }
                @Override
                public void onFailure(Exception e) {
                    Log.e("MainActivity", "Failed to load disliked items", e);
                }
            });

            firestoreHelper.loadUserReportedItems(userId, new FirestoreHelper.FirestoreCallback() {
                @Override
                public void onSuccess(List<String> items) {
                    if (items != null) {
                        reportedItems.addAll(items);
                        if (registeredAdapter != null) registeredAdapter.setReportedItems(reportedItems);
                    }
                }
                @Override
                public void onFailure(Exception e) {
                    Log.e("MainActivity", "Failed to load reported items", e);
                }
            });
        }
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

    // Provide a way for the fragment to register the adapter
    public void registerAdapter(ExpandableRecyclerViewAdapter adapter) {
        this.registeredAdapter = adapter;
        if (registeredAdapter != null) {
            registeredAdapter.setLikedItems(likedItems);
            registeredAdapter.setDislikedItems(dislikedItems);
            registeredAdapter.setReportedItems(reportedItems);
            // push any already-fetched images
            for (Map.Entry<String, byte[]> e : imageBytesMap.entrySet()) {
                registeredAdapter.setImageBytes(e.getKey(), e.getValue());
            }
        }
        pushFlatListToAdapter();
    }


    @Override
    public void onLikeClicked(String foodId) {
        if (foodId == null) return;
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId == null) return;

        boolean nowLiked = !likedItems.contains(foodId);
        if (nowLiked) {
            likedItems.add(foodId);
            dislikedItems.remove(foodId);
        } else {
            likedItems.remove(foodId);
        }

        if (registeredAdapter != null) {
            registeredAdapter.setLikedItems(likedItems);
            registeredAdapter.setDislikedItems(dislikedItems);
        }

        FirestoreHelper firestoreHelper = new FirestoreHelper();
        firestoreHelper.saveUserLikedItems(userId, new ArrayList<>(likedItems));
        firestoreHelper.saveUserDislikedItems(this, userId, new ArrayList<>(dislikedItems));
    }

    @Override
    public void onDislikeClicked(String foodId) {
        if (foodId == null) return;
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId == null) return;

        boolean nowDisliked = !dislikedItems.contains(foodId);
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

        FirestoreHelper firestoreHelper = new FirestoreHelper();
        firestoreHelper.saveUserLikedItems(userId, new ArrayList<>(likedItems));
        firestoreHelper.saveUserDislikedItems(this, userId, new ArrayList<>(dislikedItems));
    }


    private void checkAndMigrateImages() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        boolean migrationDone = prefs.getBoolean("image_migration", false);

        Log.d("Migration", "Migration check started. Already done? " + migrationDone);

        if (!migrationDone) {
            FirestoreHelper firestoreHelper = new FirestoreHelper();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Log.d("Migration", "Starting migration - querying food_images collection");

            db.collection("food_images")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        int totalDocs = querySnapshot.size();
                        int base64Count = 0;

                        Log.d("Migration", "Found " + totalDocs + " documents in food_images");
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            Log.d("Migration", "Checking document: " + doc.getId() +
                                    ", has imageBase64: " + doc.contains("imageBase64") +
                                    ", has imageUrl: " + doc.contains("imageUrl"));
                            if (doc.contains("imageBase64")) {
                                firestoreHelper.migrateImageToStorage(doc.getId());
                                Log.d("Migration", "Migrating image for: " + doc.getId());
                            }
                        }
                        Log.d("Migration", "Migration completed. Processed " + base64Count + " images");
                        // Mark migration as complete
                        prefs.edit().putBoolean("image_migration", true).apply();
                        Log.d("Migration", "Image migration completed");
                    })
                    .addOnFailureListener(e ->
                            Log.e("Migration", "Failed to fetch food images", e));
        } else {
            Log.d("Migration", "Skipping migration - already completed");
        }
    }

    @Override
    public void onReportImageClicked(String foodId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        if (foodId == null || userId == null) return;

        boolean isAlreadyReported = reportedItems.contains(foodId);

        if (isAlreadyReported) {
            reportedItems.remove(foodId);
        } else {
            reportedItems.add(foodId);
        }

        if (registeredAdapter != null) {
            registeredAdapter.setReportedItems(reportedItems);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirestoreHelper firestoreHelper = new FirestoreHelper();

        firestoreHelper.saveUserReportedItems(userId, new ArrayList<>(reportedItems));

        db.runTransaction(tx -> {
            DocumentReference ref = db.collection("food_images").document(foodId);
            DocumentSnapshot snap = tx.get(ref);

            List<String> flaggedBy = (List<String>) snap.get("flaggedBy");
            Long flagCount = snap.getLong("flagCount");
            if (flaggedBy == null) flaggedBy = new ArrayList<>();
            if (flagCount == null) flagCount = 0L;

            if (isAlreadyReported) {
                // Undo report
                if (flaggedBy.contains(userId)) {
                    flaggedBy.remove(userId);
                    long newCount = Math.max(0L, flagCount - 1L);
                    tx.update(ref, "flaggedBy", flaggedBy, "flagCount", newCount);
                }
                return false;
            } else {
                // Add report
                if (flaggedBy.contains(userId)) return false;

                flaggedBy.add(userId);
                long newCount = flagCount + 1L;

                tx.update(ref, "flaggedBy", flaggedBy, "flagCount", newCount);
                return newCount >= 5L;
            }
        }).addOnSuccessListener(shouldRegenerate -> {
            if (Boolean.TRUE.equals(shouldRegenerate)) {
                String foodName = foodId;
                if (menu != null) {
                    foodName = findFoodNameById(foodId);
                }
                String prompt = FoodMenuFragment.buildNanobananaPrompt(foodName, null);

                firestoreHelper.regenerateImageForFood(foodId, prompt, registeredAdapter,
                        new FirestoreHelper.FirestoreImageCallback() {
                            @Override
                            public void onSuccess(byte[] generatedBytes) {
                                Log.d("MainActivity", "Image regenerated after 5 flags");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.e("MainActivity", "Failed to regenerate image", e);
                            }
                        });
            }
        });
    }


    private String findFoodNameById(String foodId) {
        if (controllerParentItems == null) return foodId;
        for (ParentItem parent : controllerParentItems) {
            if (parent == null || parent.getChildItems() == null) continue;
            for (Object child : parent.getChildItems()) {
                if (child instanceof edu.vassar.cmpu203.vassareats.model.FoodItem) {
                    edu.vassar.cmpu203.vassareats.model.FoodItem fi =
                            (edu.vassar.cmpu203.vassareats.model.FoodItem) child;
                    if (foodId.equals(fi.getFoodId())) {
                        return fi.getFoodItemName();
                    }
                }
            }
        }
        return foodId;
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




