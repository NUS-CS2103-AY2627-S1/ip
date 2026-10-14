package verity.ui;

import java.util.Scanner;

/**
 * Handles all interaction with the user: printing messages to the console
 * and reading command input.
 */
public class Ui {
    /** Greeting shown to the user before any input, on both the CLI and the GUI. */
    public static final String GREETING = "Hi I'm Verity, your personal assistant.\n"
            + "Ask me anything, I know everything!";

    private static final String DIVIDER = "____________________________________________________________";

    private static final String BANNER = "__     __            _  _          \n"
            + "\\ \\   / / ___  _ __ (_)| |_  _   _ \n"
            + " \\ \\ / / / _ \\| '__|| || __|| | | |\n"
            + "  \\ V / |  __/| |   | || |_ | |_| |\n"
            + "   \\_/   \\___||_|   |_| \\__| \\__, |\n"
            + "                             |___/ \n";

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Prints the welcome banner and greeting.
     */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println(GREETING);
        System.out.println(DIVIDER);
    }

    /**
     * Reads the next line of user input.
     *
     * @return Line of input typed by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints the divider line used to separate one command's output from the next.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Prints an error message in the standard error format.
     *
     * @param message Message explaining what went wrong.
     */
    public void showError(String message) {
        System.out.println("ERROR!!! >.<\n" + message);
    }

    /**
     * Prints a command's response.
     *
     * @param response Response to print.
     */
    public void showResponse(String response) {
        System.out.println(response);
    }

    /**
     * Prints a warning that saved tasks could not be loaded from disk.
     *
     * @param reason Reason the load failed.
     */
    public void showLoadWarning(String reason) {
        System.out.println("Warning: could not load saved tasks (" + reason + ")");
    }
}
