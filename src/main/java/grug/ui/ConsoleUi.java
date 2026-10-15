package grug.ui;

import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

/**
 * Represents a display that can be read from and written to.
 */
public class ConsoleUi implements Ui {
    private final Scanner reader;
    private final PrintWriter writer;

    /**
     * Creates a new display that reads with the scanner and writes with the writer.
     *
     * @param reader The {@link Scanner} to read from.
     * @param writer The {@link PrintWriter} to write to.
     */
    public ConsoleUi(Scanner reader, PrintWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    /**
     * Displays the text and then a newline.
     *
     * @param message The text to display.
     */
    @Override
    public void display(String message) {
        this.writer.println(message);
        this.writer.flush();
    }

    /**
     * Displays the text formatted by args, then a newline.
     *
     * @param fmtString The text to format then display.
     * @param args      The arguments to the format string.
     */
    @Override
    public void display(String fmtString, Object... args) {
        this.writer.println(fmtString.formatted(args));
        this.writer.flush();
    }

    /**
     * Displays the message in red.
     *
     * @param message The message to display.
     */
    @Override
    public void displayError(String message) {
        this.writer.print("\033[31m");
        this.writer.println(message);
        this.writer.print("\033[0m");
        this.writer.flush();
    }

    /**
     * Displays the message in yellow.
     *
     * @param message The message to display.
     */
    @Override
    public void displayWarning(String message) {
        this.writer.print("\033[33m");
        this.writer.println(message);
        this.writer.print("\033[0m");
        this.writer.flush();
    }

    /**
     * Displays the prompt (without adding a newline), then reads the input.
     *
     * @param prompt The prompt to display, e.g. {@code "Enter a word: "}.
     * @return An optional containing stripped the user's input, or an empty
     *         optional if no line was found (e.g. ctrl+c pressed).
     * @throws IllegalStateException If the reader was closed.
     */
    @Override
    public Optional<String> readInput(String prompt) {
        this.writer.print(prompt);
        this.writer.flush();

        try {
            return Optional.of(this.reader.nextLine().strip());
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }
}
