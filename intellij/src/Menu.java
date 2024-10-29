package intellij.src;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import static java.lang.System.out;

public class Menu {
    JSONObject jsonMenuObject;
    public Menu(String urlStr) throws ParseException {
        Request request = new Request(urlStr);
        request.getWebPage();
        this.jsonMenuObject = request.getJsonMenu();
    }

    public boolean getMenu() {
        out.println("Menu Options for the day");
        out.println(jsonMenuObject);
        return false;
    }
}
