package grug.ui;

import java.util.Optional;

/**
 * Represents a User Interface that can display text and read user input.
 */
public interface Ui {
    /**
     * Displays the text to the ui.
     *
     * @param message The text to display.
     */
    void display(String message);

    /**
     * Displays the text formatted by args.
     *
     * @param fmtString The text to format then display.
     * @param args      The arguments to the format string.
     */
    void display(String fmtString, Object... args);

    /**
     * Displays an error message.
     *
     * @param message The message to display.
     */
    void displayError(String message);

    /**
     * Displays a warning message.
     *
     * @param message The message to display.
     */
    void displayWarning(String message);

    /**
     * Displays the prompt (without adding a newline), then reads the input.
     *
     * @param prompt The prompt to display, e.g. {@code "Enter a word: "}.
     * @return An optional containing stripped the user's input, or an empty
     *         optional if no line was found (e.g. ctrl+c pressed).
     * @throws IllegalStateException If the reader was closed.
     */
    Optional<String> readInput(String prompt);
}
