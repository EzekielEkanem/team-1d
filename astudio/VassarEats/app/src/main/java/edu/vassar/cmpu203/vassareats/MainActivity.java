package edu.vassar.cmpu203.vassareats;

import android.os.Bundle;
import android.util.Log;
import android.os.StrictMode;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;

import java.text.ParseException;
import java.util.List;

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
            Log.e("Testing", "Menu on load \n" + menu);
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
    public void onAddPreferenceList(List<Preference.Preferences> preferenceList) throws JSONException, ParseException {
        menu.changePreferences(preferenceList);
//        Log.e("Testing", "Menu should be updated \n" + menu.toString());
        selectPreferenceView.updateMenu(menu.getMenu(), menu.getPreferences());
    }
}