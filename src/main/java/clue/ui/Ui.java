package clue.ui;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Scanner;

/**
 * Handles all user interaction, both input and output.
 */
public class Ui extends PrintWriter {
    public static final String BANNER = """
              _____ _      _    _ _____ \s
             / ____| |    | |  | |  ___|\s
            | |    | |    | |  | | |___ \s
            | |    | |    | |  | |  ___|\s
            | |____| |____| |__| | |___ \s
             \\_____|______|\\____/|_____|\s
            """;
    public static final String SEPARATOR = "____________________________________________________________";
    private final InputHandler handler;

    /** Interface for input handling callback */
    @FunctionalInterface
    public interface InputHandler {
        /**
         * Callback for when user input is given.
         *
         * @param ui - The {@link Ui} object that received this input. Write back to it to respond to the user.
         * @param input - The text that the user gives us.
         * @return Whether to continue listening for input.
         */
        boolean handle(Ui ui, String input);
    }

    private final InputStream in;

    /**
     * Creates a {@link Ui} object with the given handler and streams.
     *
     * @param handler - The callback to respond to user input.
     * @param in - The stream to listen to for user input.
     * @param out - The stream to return output to the user.
     */
    public Ui(InputHandler handler, InputStream in, OutputStream out) {
        super(out, true);
        this.in = in;
        this.handler = handler;
    }

    /**
     * Creates a {@link Ui} object with the given handler stream and writer.
     *
     * @param handler - The callback to respond to user input.
     * @param in - The stream to listen to for user input.
     * @param out - The stream to return output to the user.
     */
    public Ui(InputHandler handler, InputStream in, Writer out) {
        super(out, true);
        this.in = in;
        this.handler = handler;
    }

    /** Prints the default separator line to the output stream. */
    public void printSeparatorLine() {
        println(SEPARATOR);
    }

    /** Prints the program preamble to the output stream. */
    void printPreamble() {
        println(BANNER);
        println("Hello! I'm Clue.");
        println("What can I do for you?");
    }

    /**
     * Begins listening for user input. This method will block.
     * Returns after the input handler returns false or the input stream empties.
     */
    public void listen() {
        printSeparatorLine();
        printPreamble();
        printSeparatorLine();

        Scanner scanner = new Scanner(in);
        boolean isRunning = true;
        while (isRunning && scanner.hasNextLine()) {
            printSeparatorLine();
            isRunning = receive(scanner.nextLine());
            printSeparatorLine();
        }
    }

    /** Triggers the input handler on receiving a line of input. */
    public boolean receive(String in) {
        return this.handler.handle(this, in);
    }
}
