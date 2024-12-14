package edu.vassar.cmpu203.vassareats;

import android.content.Context;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recylerViewAdapter = new ExpandableRecyclerViewAdapter(this, this);
        recylerViewAdapter.setParentItems(menu.getFilteredMenuParentItems());
        recyclerView.setAdapter((RecyclerView.Adapter) recylerViewAdapter);

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