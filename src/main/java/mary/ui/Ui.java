package mary.ui;

import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Handles MARY's interaction with the user.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String BANNER = " /\\_/\\\n"
            + "( o.o )   M A R Y\n"
            + " > ^ <    " + Personality.TAGLINE + ".";
    private final Consumer<String> output;
    private final boolean hasDividers;
    private Scanner scanner;

    /**
     * Creates the console UI, using the current standard output stream for each reply.
     */
    public Ui() {
        this(message -> System.out.println(message), true);
    }

    /**
     * Routes replies to a chosen destination, without requiring console input.
     *
     * @param output consumer accepting each complete line of output.
     * @param hasDividers whether to include console separator lines.
     */
    public Ui(Consumer<String> output, boolean hasDividers) {
        this.output = output;
        this.hasDividers = hasDividers;
    }

    /**
     * Delivers a line to the console or graphical chat transcript.
     *
     * @param message text to display, including any intentional indentation.
     */
    public void showMessage(String message) {
        output.accept(message);
    }

    /**
     * Displays zero or more messages in order, preserving their text and spacing.
     *
     * @param messages non-null array of lines to display; may be empty.
     */
    public void showMessages(String... messages) {
        for (String message : messages) {
            showMessage(message);
        }
    }

    /**
     * Displays the startup greeting.
     */
    public void showWelcome() {
        showLine();
        showMessages(BANNER, "", Personality.PROMPT);
        showLine();
    }

    /**
     * Reads a line without trimming whitespace.
     *
     * @return the input line, or null at end-of-input.
     */
    public String readCommand() {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /**
     * Displays the standard divider.
     */
    public void showLine() {
        if (hasDividers) {
            showMessage(SEPARATOR);
        }
    }

    /**
     * Displays an error message with the standard error prefix.
     *
     * @param message explanation and any corrective guidance.
     */
    public void showError(String message) {
        showMessage(" Error: " + message);
    }

    /**
     * Displays a startup/loading error followed by a divider.
     *
     * @param message explanation of the loading problem.
     */
    public void showLoadingError(String message) {
        showError(message);
        showLine();
    }

    /**
     * Displays the exit message.
     */
    public void showGoodbye() {
        showMessage(Personality.GOODBYE);
        showLine();
    }
}
