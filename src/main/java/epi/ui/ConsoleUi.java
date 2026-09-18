package epi.ui;

import java.util.Scanner;

/** Handles console input and output for the application. */
public class ConsoleUi {
    private final Scanner scanner;

    /** Creates a console UI connected to standard input and output. */
    public ConsoleUi() {
        scanner = new Scanner(System.in);
    }

    /**
     * Checks whether another input line is available, waiting for input if necessary.
     *
     * @return {@code true} if another line can be read; {@code false} at the end of input.
     * @throws IllegalStateException If the scanner has already been closed.
     */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and returns the next input line.
     *
     * @return The next line without its terminating line separator.
     * @throws java.util.NoSuchElementException If no input line remains.
     * @throws IllegalStateException If the scanner has already been closed.
     */
    public String readLine() {
        return scanner.nextLine();
    }

    /**
     * Prints a message to standard output followed by a line separator.
     *
     * @param message Text to display, which may contain embedded line breaks.
     */
    public void showLine(String message) {
        System.out.println(message);
    }

    /** Closes the underlying scanner and its standard-input stream. Repeated calls have no effect. */
    public void close() {
        scanner.close();
    }
}
