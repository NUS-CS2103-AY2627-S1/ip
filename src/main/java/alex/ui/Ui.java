package alex.ui;

import java.util.Scanner;

/**
 * Handles console input and output for Alex.
 */
public class Ui {
    private static final String DIVIDER =
            "____________________________________________________________";
    private static final String BANNER = "    _    _           \n"
            + "   / \\  | | _____  __\n"
            + "  / _ \\ | |/ _ \\ \\/ /\n"
            + " / ___ \\| |  __/>  < \n"
            + "/_/   \\_\\_|\\___/_/\\_\\\n";

    private final Scanner scanner;

    /**
     * Creates a console UI that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Alex's welcome message.
     */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.print(BANNER);
        System.out.println("Hey. Welcome to Alex's.");
        System.out.println("What can I get started for you?");
        System.out.println(DIVIDER);
    }

    /**
     * Checks whether another command is available.
     *
     * @return whether another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next user command.
     *
     * @return the next command.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays a response from Alex.
     *
     * @param response response to display.
     */
    public void showResponse(String response) {
        showMessage(response);
    }

    /**
     * Displays a user-facing error message.
     *
     * @param message explanation of the error.
     */
    public void showError(String message) {
        showMessage(message);
    }

    /**
     * Displays an error that prevented saved tasks from loading.
     *
     * @param message explanation of the loading error.
     */
    public void showLoadingError(String message) {
        System.out.println(message);
        System.out.println("Please repair or remove the data file, then restart Alex.");
        System.out.println(DIVIDER);
    }

    private void showMessage(String message) {
        System.out.println(message);
        System.out.println(DIVIDER);
    }
}
