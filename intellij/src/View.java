package intellij.src;

import java.util.ArrayList;

import static java.lang.System.err;
import static java.lang.System.out;

public class View {
    public ArrayList<String> errorOutput = new ArrayList<String>();
    public View (){
        errorOutput.add("You entered the wrong number. Please enter numbers between 1 and 8 inclusive");
        errorOutput.add("Please enter a valid number");
        errorOutput.add("You haven't selected any preferences yet. Please try again");
    }

    public void startUpApp() {
        out.println("Welcome to Vassar Eats");
    }

    public void promptFilter(){
        out.println("Please, choose your food preference");
        out.println("1. Vegetarian \n2. Vegan \n3. Halal \n4. Kosher \n5. Made without gluten-containing ingredients " +
                "\n6. Humane \n7. Farm to Fork \n8. I don't have any dietary preferences");
        out.println("To select preferences, write the number corresponding to the preference you want to select. To select " +
                "multiple preferences, add a comma before writing the next number");
    }

    public void outputError(int errorCode){
        out.println(errorOutput.get(errorCode));
    }

    public void printMenu(String menu) {
        out.println(menu);
    }
}
