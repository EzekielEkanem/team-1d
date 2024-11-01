package intellij.src;

import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import static java.lang.System.in;
import static java.lang.System.out;

public class Main {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(in);
        out.println("Welcome to Vassar Eats");
        ArrayList<Integer> preferencesList = new ArrayList<Integer>();

        boolean reselectPreferences = true;
        while (reselectPreferences) {
            out.println("Please, choose your food preference");
            out.println("1. Vegetarian \n2. Vegan \n3. Halal \n4. Kosher \n5. Made without gluten-containing ingredients " +
                    "\n6. Humane \n7. Farm to Fork \n8. I don't have any dietary preferences");
            out.println("To select preferences, write the number corresponding to the preference you want to select. To select " +
                    "multiple preferences, add a comma before writing the next number");
            String preferences = scanner.next();
            if (preferences.length() > 0) {
                if (preferences.length() > 1) {
                    try {
                        String[] preferencesArray = preferences.split(",");
                        for (String p : preferencesArray) {
                            if (Integer.parseInt(p) <= 0 || Integer.parseInt(p) >= 9) {
                                out.println("You entered the wrong number. Please enter numbers between 1 and 8 inclusive");
                                break;
                            }
                            else {
                                preferencesList.add(Integer.parseInt(p));
                                reselectPreferences = false;
                            }
                        }
                    }
                    catch (Exception e) {
                        out.println("Please enter a comma separated list of preferences");
                    }
                }
                else {
                    try{
                        preferencesList.add(Integer.parseInt(preferences));
                        reselectPreferences = false;
                    }
                    catch (Exception e) {
                        out.println("Please enter the number corresponding to the preference you want to select.");
                    }
                }
            }
            else {
                out.println("You haven't selected any preferences yet. Please try again");
            }
        }

        Preference preference = new Preference(preferencesList);

        Menu menu = new Menu("https://vassar.cafebonappetit.com/cafe/gordon/2024-10-31/", preference);
        menu.getMenu2();
    }
}