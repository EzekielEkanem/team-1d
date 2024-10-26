package intellij.src;

import static java.lang.System.out;

public class Main {
    public static void main(String[] args) {
        out.println("Welcome to Vassar Eats");
        out.println(Request.getWebPage("https://vassar.cafebonappetit.com/cafe/gordon/2024-10-26/"));
    }
}