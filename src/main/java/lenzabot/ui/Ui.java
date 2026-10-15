package lenzabot.ui;

import java.util.Scanner;

/**
 * Handles all interaction with the user through the console, such as
 * showing messages and reading commands.
 */
public class Ui {
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Creates a console UI that reads from standard input.
     */
    public Ui() {
    }

    /**
     * Shows the startup banner and greeting shown when the program starts.
     */
    public void showWelcome() {
        String banner = """
                ██      ███████ ███    ██ ███████  █████  ██████   ██████  ████████
                ██      ██      ████   ██    ███  ██   ██ ██   ██ ██    ██    ██
                ██      █████   ██ ██  ██   ███   ███████ ██████  ██    ██    ██
                ██      ██      ██  ██ ██  ███    ██   ██ ██   ██ ██    ██    ██
                ███████ ███████ ██   ████ ███████ ██   ██ ██████   ██████     ██
                """;

        System.out.println(banner);
        System.out.println("Hi. This is Lenza. What do you want to do?");
    }

    /**
     * Reads one command line from the user after printing the input prompt.
     * The returned line is already trimmed of surrounding whitespace.
     *
     * @return Trimmed command entered by the user.
     */
    public String readCommand() {
        System.out.print(">  ");
        return scanner.nextLine().trim();
    }

    /**
     * Shows a normal message to the user.
     *
     * @param message Message to show.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Shows an error message to the user.
     *
     * @param message Error details to show after the standard prefix.
     */
    public void showError(String message) {
        System.out.println("Oops: " + message);
    }
}
