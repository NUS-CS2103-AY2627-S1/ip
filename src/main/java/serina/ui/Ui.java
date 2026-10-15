package serina.ui;

import java.util.Scanner;

/**
 * Handles console input and output for Serina.
 */
public class Ui implements AutoCloseable {
    private static final String LINE = "    ____________________________________________________________";
    private static final String MESSAGE_PREFIX = "     ";

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return {@code true} if another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command entered by the user.
     *
     * @return The trimmed command.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays a message in Serina's standard response box.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        printLine();
        for (String line : message.split("\\R", -1)) {
            System.out.println(MESSAGE_PREFIX + line);
        }
        printLine();
    }

    /**
     * Releases the scanner used to read console input.
     */
    @Override
    public void close() {
        scanner.close();
    }

    /**
     * Displays Serina's response boundary.
     */
    private void printLine() {
        System.out.println(LINE);
    }
}
