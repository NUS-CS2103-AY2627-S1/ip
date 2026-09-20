package fifi;

import java.util.Scanner;

/**
 * Handles all console input and output for the Fifi chatbot.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String RESPONSE_FORMAT = LINE + "%n"
            + "%s%n"
            + LINE + "%n";
    private static final String BANNER = """
            _____ _  __ __
            |  ___(_)/ _(_)
            | |_  | | |_| |
            |  _| | |  _| |
            |_|   |_|_| |_|
            """;
    private static final String GREETING = LINE + "\n"
            + "Hello! My name is Fifi ^^\n"
            + "How may I help?\n"
            + LINE + "\n";

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Shows the chatbot banner and greeting.
     */
    public void showWelcome() {
        System.out.print(BANNER);
        System.out.print(GREETING);
    }

    /**
     * Reads the next full command line typed by the user.
     *
     * @return the full command line
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Returns whether another command is available before reading it.
     *
     * @return false when console input has ended
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Shows one or more chatbot response messages on separate lines.
     *
     * @param messages the messages to show between separator lines
     */
    public void showResponse(String... messages) {
        String response = String.join(System.lineSeparator(), messages);
        System.out.printf(RESPONSE_FORMAT, response);
    }

    /**
     * Shows an error with a heading that distinguishes it from normal responses.
     *
     * @param message explanation of the error
     */
    public void showError(String message) {
        showResponse("[ERROR]", message);
    }
}
