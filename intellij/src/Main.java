package intellij.src;

import org.json.simple.parser.ParseException;

import static java.lang.System.out;

public class Main {
    public static void main(String[] args) throws ParseException {
        out.println("Welcome to Vassar Eats");
        Menu menu = new Menu("https://vassar.cafebonappetit.com/cafe/gordon/2024-10-29/");
        out.println(menu.getMenu());
    }
}