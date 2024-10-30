package intellij.src;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import static java.lang.System.out;

public class Menu {
    JSONObject jsonMenuObject;
    HashMap<String, Object> FoodItems = new HashMap<String, Object>();
    public Menu(String urlStr) throws ParseException {
        Request request = new Request(urlStr);
        request.getWebPage();
        this.jsonMenuObject = request.getJsonMenu();
    }

    public <JSONElement> boolean getMenu() {
        for (Object key : jsonMenuObject.keySet()) {
            String keyStr = (String) key;
            JSONObject value = (JSONObject) jsonMenuObject.get(keyStr);

            //System.out.println(keyStr + " : " + value);

            FoodItem newFood = new FoodItem(keyStr, value);
        }
        return false;
    }
}
