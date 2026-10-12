package duke;

import java.util.Scanner;

/**
 * Handles all interactions with the user: reading commands and showing output.
 */
public class Ui {
    private final Scanner scanner;

    /**
     * Creates a Ui that reads user input from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads the next line of input typed by the user.
     *
     * @return the raw command line
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows the welcome banner followed by the given greeting.
     *
     * @param greeting the greeting text to display
     */
    public void showWelcome(String greeting) {
        Banner.print();
        System.out.println(greeting);
    }

    /**
     * Shows an error message to the user.
     *
     * @param message the error message to display
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Shows a general message to the user.
     *
     * @param message the message to display
     */
    public void show(String message) {
        System.out.println(message);
    }
}
