package alice.ui;

import java.util.Scanner;

/**
 * Handles Alice's console input and output.
 */
public class Ui {
    private static final String SEPARATOR = "_______________________________________";
    private static final String WELCOME_MESSAGE = SEPARATOR + "\n"
            + "    _    _     ___ ____ _____ \n"
            + "   / \\  | |   |_ _/ ___| ____|\n"
            + "  / _ \\ | |    | | |   |  _|  \n"
            + " / ___ \\| |___ | | |___| |___ \n"
            + "/_/   \\_\\_____|___\\____|_____|\n"
            + "Hello! I'm Alice.\n"
            + "What can I do for you?\n"
            + SEPARATOR;
    private final Scanner scanner;

    /**
     * Creates the user interface that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Alice's welcome banner.
     */
    public void showWelcome() {
        System.out.println(WELCOME_MESSAGE);
    }

    /**
     * Returns the welcome banner used by Alice's user interfaces.
     *
     * @return The formatted welcome banner.
     */
    public static String getWelcomeMessage() {
        return WELCOME_MESSAGE;
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return The command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays a line separating console messages.
     */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /**
     * Displays a response produced by Alice.
     *
     * @param response The response to display.
     */
    public void showResponse(String response) {
        System.out.println(response);
        showSeparator();
    }

}
