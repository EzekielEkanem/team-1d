package edu.vassar.cmpu203.vassareats;

import android.os.Bundle;
import android.os.StrictMode;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.vassar.cmpu203.vassareats.model.FoodItem;
import edu.vassar.cmpu203.vassareats.model.Menu;
import edu.vassar.cmpu203.vassareats.view.MyAdapter;
import edu.vassar.cmpu203.vassareats.model.Preference;
import edu.vassar.cmpu203.vassareats.view.IMenuView;
import edu.vassar.cmpu203.vassareats.view.MenuView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements IMenuView.Listener{
//    Initialize variable
    public IMenuView menuView;
    Menu menu;

    public MainActivity() throws JSONException, ParseException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            menu = new Menu();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        this.menuView = new MenuView(this, this);
        setContentView(menuView.getRootView());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        selectPreferenceView.updateMenu(menu.getMenu(), menu.getPreferences());

        HashSet<String> dietLabels = new HashSet<String>();
        dietLabels.add("Vegan");

        List<FoodItem> itemList = new ArrayList<>();
        itemList.add(new FoodItem("Alice", "Class of 2024", dietLabels));
        itemList.add(new FoodItem("Bob", "Class of 2025", dietLabels));
        itemList.add(new FoodItem("Charlie", "Class of 2026", dietLabels));
        itemList.add(new FoodItem("Alice", "Class of 2024", dietLabels));
        itemList.add(new FoodItem("Bob", "Class of 2025", dietLabels));
        itemList.add(new FoodItem("Charlie", "Class of 2026", dietLabels));
        itemList.add(new FoodItem("Alice", "Class of 2024", dietLabels));
        itemList.add(new FoodItem("Bob", "Class of 2025", dietLabels));
        itemList.add(new FoodItem("Charlie", "Class of 2026", dietLabels));
        itemList.add(new FoodItem("Alice", "Class of 2024", dietLabels));
        itemList.add(new FoodItem("Bob", "Class of 2025", dietLabels));
        itemList.add(new FoodItem("Charlie", "Class of 2026", dietLabels));
        itemList.add(new FoodItem("Alice", "Class of 2024", dietLabels));
        itemList.add(new FoodItem("Bob", "Class of 2025", dietLabels));
        itemList.add(new FoodItem("Charlie", "Class of 2026", dietLabels));
        itemList.add(new FoodItem("Alice", "Class of 2024", dietLabels));
        itemList.add(new FoodItem("Bob", "Class of 2025", dietLabels));
        itemList.add(new FoodItem("Charlie", "Class of 2026", dietLabels));
        itemList.add(new FoodItem("Alice", "Class of 2024", dietLabels));
        itemList.add(new FoodItem("Bob", "Class of 2025", dietLabels));
        itemList.add(new FoodItem("Charlie", "Class of 2026", dietLabels));
        itemList.add(new FoodItem("Alice", "Class of 2024", dietLabels));
        itemList.add(new FoodItem("Bob", "Class of 2025", dietLabels));
        itemList.add(new FoodItem("Charlie", "Class of 2026", dietLabels));

        // Set up the RecyclerView with LayoutManager and Adapter
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(this, itemList));

    }

    @Override
    public void updatePreferences(List<Preference.Preferences> preferenceList) {
        menu.changePreferences(preferenceList);
        menuView.updateMenu(menu.getMenu(), menu.getPreferences());
    }

    @Override
    public void updateDate(LocalDate date) throws JSONException, ParseException {
        menu.updateDate(date);
        menuView.updateMenu(menu.getMenu(), menu.getPreferences());
    }
}