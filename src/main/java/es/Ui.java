package es;

import java.util.Scanner;

/** Handles all console input and output for Es. */
public class Ui {
    private static final String INDENT = "    ";
    private static final String DIVIDER = INDENT + "____________________________________________________________";
    private final Scanner scanner = new Scanner(System.in);

    /** Displays the welcome message. */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.print(INDENT + " _____     \n" + INDENT + "| ____|___ \n" + INDENT + "|  _| / __|\n"
                + INDENT + "| |___\\__ \\\n" + INDENT + "|_____|___/\n");
        System.out.println(INDENT + "Welcome to the grand library of dreams, Somniareves.");
        System.out.println(INDENT + "What can I do for you?");
        showLine();
    }

    /** Reads the next command, or returns null at end of input. */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /** Displays the standard divider. */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /** Displays a chatbot response. */
    public void show(String message) {
        System.out.println(INDENT + message);
    }

    /** Displays an error response. */
    public void showError(String message) {
        show(message);
    }

    /** Returns the indentation used for multiline task details. */
    public String indent() {
        return INDENT;
    }
}
