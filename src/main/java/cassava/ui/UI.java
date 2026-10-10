package cassava.ui;

import java.util.Scanner;

/**
 * Handles input and output to the console.
 */
public class UI {

    /**
     * Prints the banner and introduction text.
     * @param name Name of the program.
     */
    public static void printIntro(String name) {
        String banner = "  ____                               \n"
                + " / ___|__ _ ___ ___  __ ___   ____ _\n"
                + "| |   / _` / __/ __|/ _` \\ \\ / / _` |\n"
                + "| |__| (_| \\__ \\__ \\ (_| |\\ V / (_| |\n"
                + " \\____\\__,_|___/___/\\__,_| \\_/ \\__,_|";
        System.out.println(banner);

        System.out.println("__________________________________________________");
        System.out.println("Hi! I'm " + name + ".");
        System.out.println("How can I help you today?");
    }

    /**
     * Prints a border to separate commands and responses.
     */
    public static void printBorder() {
        System.out.println("__________________________________________________");
    }

    /**
     * Scans user input.
     * @return raw user input as a String.
     */
    public static String scanUserInput() {
        Scanner scanner = new Scanner(System.in);

        printBorder();
        String input = scanner.nextLine();
        return input;
    }
}
