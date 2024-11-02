package intellij.src;

import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.System.in;

public class Controller {
    View view = new View();
    Scanner scanner = new Scanner(in);
    ArrayList<Integer> preferencesList = new ArrayList<Integer>();
    Menu menu = new Menu();

    public Controller () throws ParseException {


    }

    public void startUpApp(){
        view.startUpApp();

        while (true) {
            view.promptFilter();

            String preferences = scanner.next();

            InputReport inputReport = menu.changePreferences(preferences);

            if (!inputReport.getStatusSuccess()) {
                view.outputError(inputReport.getErrorCode());
                continue;
            }

            menu.updateMenu();

            view.printMenu(menu.toString());
        }
    }
}

