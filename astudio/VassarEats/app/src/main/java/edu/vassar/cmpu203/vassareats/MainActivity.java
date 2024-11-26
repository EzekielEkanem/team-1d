package edu.vassar.cmpu203.vassareats;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;

import java.text.ParseException;
import java.util.List;

import edu.vassar.cmpu203.vassareats.model.InputReport;
import edu.vassar.cmpu203.vassareats.model.Menu;
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
        InputReport inputReport = menu.changePreferences(preferenceList);
        menu.updateMenu();
    }
}