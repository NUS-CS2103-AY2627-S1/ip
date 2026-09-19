package yawned.ui;

import java.util.Scanner;

/**
 * Handles all console interaction with the user.
 */
public class Ui {
    private static final String BANNER = """
            ========================
                     YAWNED
             Reluctantly organized
            ========================
            """;

    private final Scanner scanner;

    /**
     * Creates a user interface that reads commands from the given scanner.
     *
     * @param scanner Source of user input.
     */
    public Ui(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Shows the chatbot greeting.
     */
    public void showWelcome() {
        showBreakLine();
        System.out.println(BANNER);
    }

    /**
     * Returns whether another command is available from the input source.
     *
     * @return Whether another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command from the user.
     *
     * @return The user's command.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows a message without requesting input.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Shows the separator between console interactions.
     */
    public void showBreakLine() {
        System.out.println("____________________________________________________________\n");
    }
}
