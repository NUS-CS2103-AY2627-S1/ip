package bob.ui;

import java.util.Scanner;


/**
 * Handles console input and presents chatbot messages to the user.
 */
public class Ui {
    private static final String LINE_PREFIX = "    ";
    private static final String MESSAGE_PREFIX = "     ";
    private static final String DIVIDER =
            LINE_PREFIX + "____________________________________________________________";
    private final Scanner scanner;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return true if another line can be read.
     */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the next input line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Closes the input stream owned by this user interface.
     */
    public void close() {
        scanner.close();
    }

    /** Displays the fixed chatbot greeting and banner. */
    public void showWelcome() {
        showLine();
        showBanner();
        System.out.println(MESSAGE_PREFIX + "Hi! I'm Bob, your task companion.");
        System.out.println(MESSAGE_PREFIX + "What can I do for you?");
        showLine();
    }

    /**
     * Displays a response produced by a command.
     *
     * @param response response to display.
     */
    public void showResponse(String response) {
        for (String line : response.split("\\R")) {
            System.out.println(MESSAGE_PREFIX + line);
        }
    }

    /**
     * Displays a divider between chatbot messages.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Displays the chatbot name as ASCII art.
     */
    private void showBanner() {
        // @formatter:off
        String banner = MESSAGE_PREFIX + " ____        _     \n"
                + MESSAGE_PREFIX + "| __ )  ___ | |__\n"
                + MESSAGE_PREFIX + "|  _ \\ / _ \\| '_ \\\n"
                + MESSAGE_PREFIX + "| |_) | (_) | |_) |\n"
                + MESSAGE_PREFIX + "|____/ \\___/|_.__/";
        // @formatter:on

        System.out.println(banner);
    }
}
