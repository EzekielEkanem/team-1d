package edu.vassar.cmpu203.vassareats;

import android.os.Bundle;
<<<<<<< HEAD
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
=======
import android.os.StrictMode;
>>>>>>> 7ae9a24fbea6a45291ab674e0d4336017cbf2335
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;

import java.text.ParseException;
import java.util.List;

import edu.vassar.cmpu203.vassareats.model.InputReport;
import edu.vassar.cmpu203.vassareats.model.Menu;
import edu.vassar.cmpu203.vassareats.model.Preference;
import edu.vassar.cmpu203.vassareats.view.ISelectPreferenceView;
import edu.vassar.cmpu203.vassareats.view.SelectPreferenceView;

public class MainActivity extends AppCompatActivity implements ISelectPreferenceView.Listener{
//    Initialize variable
    ISelectPreferenceView selectPreferenceView;
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
        this.selectPreferenceView = new SelectPreferenceView(this, this);
        setContentView(selectPreferenceView.getRootView());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    @Override
    public void onAddPreferenceList(List preferenceList) throws JSONException, ParseException {
<<<<<<< HEAD
        // Make the text screen
        LinearLayout mainLayout = findViewById(R.id.main);

//        SelectPreferenceView newView = new SelectPreferenceView(this, this);

        // Create a new TextView
        TextView diningStationTextView = new TextView(this);

// Set the attributes
        diningStationTextView.setId(View.generateViewId()); // Dynamically generate an ID
        diningStationTextView.setLayoutParams(new LinearLayout.LayoutParams(
                300, // Width in dp (converted below)
                20   // Height in dp (converted below)
        ));

// Set the specific properties
        diningStationTextView.setGravity(Gravity.CENTER); // Center text inside the TextView
        diningStationTextView.setText(R.string.welcome); // Set text from strings.xml
        diningStationTextView.setTextSize(18); // Set text size in sp

// Convert dp to pixels for margin and width/height
        int marginInPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        int widthInPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
        int heightInPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());

// Update layout parameters with margin and specific dimensions
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthInPixels, heightInPixels);
        params.gravity = Gravity.CENTER_HORIZONTAL; // Center horizontally in parent layout
        params.topMargin = marginInPixels; // Add top margin
        diningStationTextView.setLayoutParams(params);

        mainLayout.addView(diningStationTextView);


//        mainLayout.addView(newView.getRootView());
//        Menu menu = new Menu();
//        InputReport inputReport = menu.changePreferences(preferenceList);
//        menu.updateMenu();
=======
        InputReport inputReport = menu.changePreferences(preferenceList);
        menu.updateMenu();
>>>>>>> 7ae9a24fbea6a45291ab674e0d4336017cbf2335
    }
}