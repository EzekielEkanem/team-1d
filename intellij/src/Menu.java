package intellij.src;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.HashMap;

import static java.lang.System.out;

public class Menu {
    JSONObject jsonMenuObject;
    HashMap<Integer, JSONObject> mealDayParts;
    public Menu(String urlStr) throws ParseException {
        Request request = new Request(urlStr);
        request.getWebPage();
        this.jsonMenuObject = request.getJsonMenu();
        this.mealDayParts = request.getMealDayParts();
    }

    public boolean getMenu() {
        out.println("Menu Options for the day");
        out.println(mealDayParts);
        return false;
    }
}
