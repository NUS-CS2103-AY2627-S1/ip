package lumine.ui;

import java.util.Scanner;

/**
 * Handles console input and output that is shared by the application loop.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER =
            " ___      __   __  __   __  ___   __    _  _______ \n"
                    + "|   |    |  | |  ||  |_|  ||   | |  |  | ||       |\n"
                    + "|   |    |  | |  ||       ||   | |   |_| ||    ___|\n"
                    + "|   |    |  |_|  ||       ||   | |       ||   |___ \n"
                    + "|   |___ |       ||       ||   | |  _    ||    ___|\n"
                    + "|       ||       || ||_|| ||   | | | |   ||   |___ \n"
                    + "|_______||_______||_|   |_||___| |_|  |__||_______|\n";

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        this(true);
    }

    /**
     * Creates a UI with optional standard-input support.
     *
     * @param isConsoleInputEnabled whether this UI reads commands from standard input.
     */
    protected Ui(boolean isConsoleInputEnabled) {
        scanner = isConsoleInputEnabled ? new Scanner(System.in) : null;
    }

    /**
     * Prints the ASCII-art banner and welcome message at application start.
     */
    public void showGreetings() {
        System.out.println(LINE + "\n" + BANNER
                + "Hello, I'm Lumine!\n"
                + "What can I do for you today?\n"
                + LINE);
    }

    /**
     * Shows the separator used around each command interaction.
     */
    public void showSeparator() {
        System.out.println(LINE);
    }

    /**
     * Shows a message to the user.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Shows each supplied message to the user in order.
     *
     * @param messages messages to show.
     */
    public void showMessages(String... messages) {
        for (String message : messages) {
            showMessage(message);
        }
    }

    /**
     * Returns whether another command is available on standard input.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command from standard input.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows the application's farewell message.
     */
    public void showExit() {
        showMessage("Bye. Hope to see you again soon!");
    }
}
