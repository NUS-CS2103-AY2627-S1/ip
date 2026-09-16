package mira.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Handles all text input and output for Mira.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";

    private final PrintStream output;
    private final Scanner scanner;

    /**
     * Creates a UI connected to standard input and output.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI connected to the supplied streams.
     *
     * @param input stream from which commands are read.
     * @param output stream to which responses are written.
     */
    public Ui(InputStream input, PrintStream output) {
        this.scanner = new Scanner(input);
        this.output = output;
    }

    /**
     * Returns whether another command is available.
     *
     * @return {@code true} if another input line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command line.
     *
     * @return the next user command.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays a response between Mira's horizontal boundaries.
     *
     * @param message Response to display.
     */
    public void showResponse(String message) {
        showBlock(message);
    }

    /**
     * Prints a response between Mira's horizontal boundaries.
     *
     * @param message Response text to display.
     */
    private void showBlock(String message) {
        output.println(LINE);
        output.println(message);
        output.println(LINE);
    }
}
