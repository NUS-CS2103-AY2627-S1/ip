package am.ui;

import java.util.Scanner;

/**
 * Handles console input and formatted console output for the application.
 */
public class Ui {
    private static final String INDENT = "    ";
    private static final String SEPARATOR = "_________________________________________________________________";
    private static final String BANNER = """
             ░▒▓██████▓▒░░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓░
            ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓░
            ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓░
            ░▒▓████████▓▒░▒▓████████▓▒░▒▓████████▓▒░▒▓████████▓▒░▒▓████████▓░
            ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓░
            ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓░
            ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓░""";

    private final Scanner scanner;

    /**
     * Creates a console user interface backed by standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Prints a message surrounded by the application's response separator.
     *
     * @param message message to print
     */
    public void printResponse(String message) {
        System.out.println(insertIndent(SEPARATOR));
        System.out.println(insertIndent(message));
        System.out.println(insertIndent(SEPARATOR));
    }

    /**
     * Displays an error message using the standard response format.
     *
     * @param message error message to display
     */
    public void showError(String message) {
        printResponse(message);
    }

    /**
     * Reads the next command from standard input.
     *
     * @return command entered by the user
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the application's welcome banner and prompt.
     */
    public void showWelcome() {
        printResponse(String.format("%s\nMy name is AM.\nWhat do you want?", BANNER));
    }

    private static String insertIndent(String message) {
        return INDENT + message.replace("\n", "\n" + INDENT);
    }
}
