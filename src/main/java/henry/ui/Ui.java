package henry.ui;

import java.util.Scanner;

/**
 * Handles console input and output for Henry.
 */
public class Ui {
    private static final String SEPARATOR =
            "____________________________________________________________";
    private static final String BANNER = """
             _   _                     \s
            | | | | ___ _ __  _ __ _   _
            | |_| |/ _ \\ '_ \\| '__| | | |
            |  _  |  __/ | | | |  | |_| |
            |_| |_|\\___|_| |_|_|   \\__, |
                                   |___/\s
            """;

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Henry's greeting.
     */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.print(BANNER);
        System.out.println("Hey, I'm Henry. What are we tackling today?");
        System.out.println(SEPARATOR);
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return true when another line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next user command.
     *
     * @return next command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays a message followed by the standard response separator.
     *
     * @param message message to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
        showSeparator();
    }

    /**
     * Displays the separator that ends a response.
     */
    private void showSeparator() {
        System.out.println(SEPARATOR);
    }
}
