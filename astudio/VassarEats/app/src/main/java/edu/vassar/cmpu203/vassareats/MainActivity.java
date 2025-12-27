package edu.vassar.cmpu203.vassareats;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View; // Import View
import android.view.ViewGroup;

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

import edu.vassar.cmpu203.vassareats.model.Menu;
import edu.vassar.cmpu203.vassareats.view.FoodMenuFragment;
import edu.vassar.cmpu203.vassareats.view.HomeFragment;
import edu.vassar.cmpu203.vassareats.view.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Menu menu;
    private MaterialToolbar topAppBar;
    private ActionBarDrawerToggle drawerToggle;

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

        setNavigationDrawerHalfWidth();
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

    private void setNavigationDrawerHalfWidth() {
        NavigationView navView = findViewById(R.id.nav_view);
        if (navView == null) return;

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int drawerWidth = screenWidth / 2; // half the screen

        // Ensure we update DrawerLayout.LayoutParams (safe cast)
        ViewGroup.LayoutParams lp = navView.getLayoutParams();
        if (lp instanceof DrawerLayout.LayoutParams) {
            DrawerLayout.LayoutParams dlp = (DrawerLayout.LayoutParams) lp;
            dlp.width = drawerWidth;
            navView.setLayoutParams(dlp);
        } else {
            lp.width = drawerWidth;
            navView.setLayoutParams(lp);
        }
    }

    public Menu getMenu() {
        return this.menu;
    }
}




