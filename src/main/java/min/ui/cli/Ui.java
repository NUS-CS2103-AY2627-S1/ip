package min.ui.cli;

import java.util.Scanner;

/**
 * Handles Min's console input and output.
 */
public class Ui {
    private static final int SEPARATOR_LENGTH = 60;
    private static final String SEPARATOR = "_".repeat(SEPARATOR_LENGTH);

    private final Scanner scanner;

    /** Creates a CLI interface that reads from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Returns whether another command is available from the user. */
    public boolean hasNextCommand() {
        return this.scanner.hasNextLine();
    }

    /** Returns the next command entered by the user with leading and trailing whitespace removed. */
    public String readCommand() {
        return this.scanner.nextLine().trim();
    }

    /**
     * Prints Min's banner and welcome message.
     *
     * @param welcomeMessage The welcome message to print.
     */
    public void showWelcome(String welcomeMessage) {
        String banner = " __  __ _       \n"
                + "|  \\/  (_)_ __  \n"
                + "| |\\/| | | '_ \\ \n"
                + "| |  | | | | | |\n"
                + "|_|  |_|_|_| |_|\n";
        System.out.println(banner);
        showSeparator();
        System.out.println(welcomeMessage);
        showSeparator();
    }

    /** Prints Min's standard separator. */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /**
     * Prints a response from Min.
     *
     * @param response The response to print.
     */
    public void showResponse(String response) {
        System.out.println(response);
        showSeparator();
    }

    /**
     * Prints an error message.
     *
     * @param message The message to display.
     */
    public void showError(String message) {
        System.out.println(" " + message);
        showSeparator();
    }
}
