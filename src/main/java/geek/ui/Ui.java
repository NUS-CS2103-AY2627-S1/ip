package geek.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Reads console commands and displays Geek's user-facing messages.
 */
public class Ui {
    private static final String LINE = "----------------------";

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a UI connected to the process's standard streams.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI using the supplied streams.
     *
     * @param input Stream from which commands are read.
     * @param output Stream for normal responses and recoverable errors.
     */
    public Ui(
            InputStream input,
            PrintStream output
    ) {
        this.scanner = new Scanner(input);
        this.output = output;
    }

    /**
     * Reads the next complete command line.
     *
     * @return Command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows the welcome message.
     */
    public void showWelcome() {
        showResponse(MessageFormatter.welcome());
    }

    /**
     * Shows one complete response within the console message boundary.
     *
     * @param message Response to show.
     */
    public void showResponse(String message) {
        printLines(LINE);
        printLines(message.split("\\R", -1));
        printLines(LINE, "");
    }

    /**
     * Closes the command scanner and its underlying input stream.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Writes any number of lines to the normal output stream in order.
     *
     * @param lines Lines to write.
     */
    private void printLines(String... lines) {
        for (String line : lines) {
            output.println(line);
        }
    }
}
