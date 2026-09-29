package tasktracker.ui;

import java.util.Scanner;

/**
 * Handles all user interface operations including reading input and displaying messages.
 */
public class UserInterface {
    private static final String NEWLINE = "\n";

    /**
     * Creates a scanner object to read user's inputs.
     */
    private final Scanner scanner;

    /**
     * Initializes a new {@code UserInterface} instance configured with a {@code System.in} scanner.
     */
    public UserInterface() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Retrieves the welcome message text for the GUI.
     *
     * @return The formatted welcome message.
     */
    public String getWelcomeMessage() {
        return Message.MSG_WELCOME;
    }

    /**
     * Retrieves the goodbye message text for the GUI.
     *
     * @return The formatted goodbye message.
     */
    public String getGoodbyeMessage() {
        return Message.MSG_GOODBYE;
    }

    /**
     * Displays the welcome message and application banner for CLI mode.
     */
    public void showWelcome() {
        System.out.println(Message.BANNER);
        showMessage(Message.MSG_WELCOME);
    }

    /**
     * Reads a line of command input from the terminal.
     *
     * @return the raw command string entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Wraps and prints one or more messages inside divider lines with standard indentation.
     *
     * @param messages Message lines or blocks to display.
     */
    public void showMessage(String... messages) {
        System.out.println(Message.DIVIDER);
        for (String message : messages) {
            System.out.println(formatIndented(message));
        }
        System.out.println(Message.DIVIDER);
    }

    /**
     * Indents every line of the given message with standard 4-space indentation.
     *
     * @param message The text block to indent.
     * @return The indented message string.
     */
    private String formatIndented(String message) {
        String newlineIndent = NEWLINE + Message.INDENT_4;
        return Message.INDENT_4 + message.replace(NEWLINE, newlineIndent);
    }

    /**
     * Displays the farewell message upon exiting the application for CLI mode.
     */
    public void showGoodbye() {
        System.out.println(Message.BANNER);
        showMessage(Message.MSG_GOODBYE);
    }

    /**
     * Displays the help guide showing all available commands and their formats.
     */
    public void showHelp() {
        showMessage(Message.MSG_HELP);
    }
}
