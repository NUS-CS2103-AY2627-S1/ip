package james.ui;

import java.util.Scanner;

/**
 * Handles user interactions by reading inputs and displaying formatted output.
 */
public class Ui {
    private static final String LINE_BREAK = "\n";
    private static final String GREETING_TITLE = "James the ぱんどろぼう\n";
    private static final String GREETING_INTRODUCTION = "Shh! I'm James, your bread thief.\n";
    private static final String GREETING_OFFER = "I'll guard your tasks. The bread? No promises!\n";
    private static final String DIVIDER = "____________________________________________________________";
    private final Scanner scanner;

    /**
     * Constructs a Ui instance and initializes the input scanner.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Reads the next line of input entered by the user.
     *
     * @return The user input string, or {@code null} if no line is available.
     */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /**
     * Checks if there is another line of input available from the user.
     *
     * @return {@code true} if another line exists, {@code false} otherwise.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Closes the underlying scanner.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Encases a message string within decorative divider lines.
     *
     * @param message The content string to encase.
     * @return The encased message string.
     */
    public String encaseMessage(String message) {
        return DIVIDER + LINE_BREAK + message + DIVIDER;
    }

    /**
     * Displays the welcome message and greeting banner.
     */
    public void greet() {
        System.out.println(encaseMessage(GREETING_TITLE +
                GREETING_INTRODUCTION +
                GREETING_OFFER) + LINE_BREAK);
    }

    /**
     * Displays a response from the shared command processor.
     *
     * @param message Response text.
     */
    public void showResponse(String message) {
        boolean hasTrailingLineBreak = message.endsWith(LINE_BREAK);
        String terminatedMessage = hasTrailingLineBreak ? message : message + LINE_BREAK;
        System.out.println(encaseMessage(terminatedMessage));
    }

}
