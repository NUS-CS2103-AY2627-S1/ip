package computa;

import java.util.Scanner;
/**
 * Handles all user interface interactions, including reading standard input and displaying messages to the terminal.
 */
public class Ui {
    private final Scanner scanner;
    private final StringBuilder output;
    /**
     * Constructs a new Ui object and initializes the system input scanner.
     */
    public Ui() {
        scanner = new Scanner(System.in);
        output = new StringBuilder();
    }
    /**
     * Displays the initial welcome greeting when the chatbot boots up.
     */
    public void showWelcome() {
        System.out.println("haiii i am computa! lmk what u need ehaha");
    }
    /**
     * Reads the next complete line of text entered by the user in the terminal.
     *
     * @return The raw string representation of the user's input.
     */
    public String readCommand() {
        return scanner.nextLine();
    }
    /**
     * Displays a standard informational message to the user.
     *
     * @param message The text to be printed to the terminal.
     */
    public void showMessage(String message) {
        System.out.println(message);
        output.append(message).append(System.lineSeparator());
    }
    /**
     * Displays an error message to the user.
     *
     * @param message The specific error description to be printed.
     */
    public void showError(String message) {
        System.out.println(message);
        output.append(message).append(System.lineSeparator());
    }

    /** Returns and clears messages collected while processing one GUI command. */
    public String collectOutput() {
        String messages = output.toString();
        output.setLength(0);
        return messages;

    }
    /**
     * Displays the parting farewell message and safely closes the system input scanner.
     */
    public void showGoodbye() {
        System.out.println("Bai Bai! How much wood could a wood chuck chuck..");
        scanner.close();
    }
}
