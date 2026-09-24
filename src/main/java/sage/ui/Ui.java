package sage.ui;

import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Handles all direct interaction between Sage and the user.
 */
public class Ui implements AutoCloseable {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER = " ____                   \n"
            + "/ ___|  __ _  __ _  ___ \n"
            + "\\___ \\ / _` |/ _` |/ _ \\\n"
            + " ___) | (_| | (_| |  __/\n"
            + "|____/ \\__,_|\\__, |\\___|\n"
            + "             |___/      \n";

    private final Scanner scanner;
    private final Consumer<String> output;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
        output = System.out::println;
    }

    /** Creates a UI that sends displayed messages to the supplied consumer.
     *
     * @param output consumer that receives displayed messages
     */
    public Ui(Consumer<String> output) {
        scanner = new Scanner(System.in);
        this.output = output;
    }

    /** Displays Sage's standard welcome message. */
    public void showWelcome() {
        showDivider();
        System.out.print(BANNER);
        show("Hello! I'm Sage.");
        show("I'm here whenever you feel like chatting!");
        showDivider();
        show("What can I do for you?");
        showDivider();
    }

    /** Displays the divider used to separate user commands and responses. */
    public void showDivider() {
        show(DIVIDER);
    }

    /** Displays one line of text to the user.
     *
     * @param message message to display
     */
    public void show(String message) {
        output.accept(message);
    }

    /** Returns the next command, or {@code null} when input has ended.
     *
     * @return next command, or {@code null} at end of input
     */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /** Closes the input scanner. */
    @Override
    public void close() {
        scanner.close();
    }
}
