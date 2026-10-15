package kiaw.ui;

import java.util.Scanner;

/**
 * Handles interaction between Kiaw and the user.
 *
 * This class reads commands from standard input and displays messages
 * through standard output.
 */
public class Ui {
    private final Scanner scanner;

    /**
     * Creates a user interface that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Kiaw's welcome message.
     */
    public void showWelcome() {
        System.out.println("Hello! I'm Kiaw.");
        System.out.println("What can I do for you?");
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return trimmed user command
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays Kiaw's goodbye message.
     */
    public void showGoodbye() {
        System.out.println(
                "Bye. Hope to see you again soon!"
        );
    }

    /**
     * Displays a message to the user.
     *
     * @param message message to display
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays an error message to the user.
     *
     * @param message description of the error
     */
    public void showError(String message) {
        System.out.println(
                "OOPS!!! " + message
        );
    }

    /**
     * Displays an error indicating that saved tasks could not be loaded.
     */
    public void showLoadingError() {
        System.out.println(
                "OOPS!!! I couldn't load your saved tasks."
        );
    }

    /**
     * Closes the input scanner used by the user interface.
     */
    public void close() {
        scanner.close();
    }
}
