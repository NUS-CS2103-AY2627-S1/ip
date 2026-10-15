package tardt.ui;

import java.util.Scanner;

import tardt.Messages;

/**
 * Class that handles interaction with the user.
 */
public class Ui {
    public static final String LINE = "____________________________________________________________";

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Shows the welcome message when the application starts.
     */
    public void showWelcome() {
        String intro = "Hello! I'm TardT. \n"
                + "What can I do for you? \n"
                + LINE;
        System.out.println(intro);
    }

    /**
     * Reads the user input.
     *
     * @return The latest user input as a String.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Checks whether another command can be read without throwing an end-of-input exception.
     *
     * @return true when a command line is available
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Prints a line in standard format in the terminal.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Shows a 'bye' message.
     */
    public void showBye() {
        System.out.println(Messages.GOODBYE_MESSAGE);
    }

    /**
     * Prints the error message in the terminal.
     *
     * @param message A String containing the error message.
     */
    public void showError(String message) {
        System.out.println(message.stripTrailing());
    }

    /**
     * Closes the Scanner object.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Prints the response message to the terminal. For CLI use.
     * @param message The response message upon user input.
     */
    public void showMessage(String message) {
        System.out.println(message.stripTrailing());
    }
}
