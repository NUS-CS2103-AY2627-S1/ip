package monkey.ui;

import java.util.Scanner;
import java.util.function.Consumer;

/** Handles console input and common user-facing messages for Monkey. */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String BANNER = " __  __              _\n"
            + "|  \\/  | ___  _ __  | | _____ _   _\n"
            + "| |\\/| |/ _ \\| '_ \\ | |/ / _ \\ | | |\n"
            + "| |  | | (_) | | | ||   <  __/ |_| |\n"
            + "|_|  |_|\\___/|_| |_||_|\\_\\___|\\__, |\n"
            + "                              |___/\n";
    private final Scanner scanner;
    private final Consumer<String> output;

    /** Creates a UI that writes messages to standard output. */
    public Ui() {
        scanner = new Scanner(System.in);
        output = System.out::println;
    }

    /** Creates a UI that sends messages to the supplied output destination. */
    public Ui(Consumer<String> output) {
        scanner = null;
        this.output = output;
    }

    /** Shows Monkey's greeting. */
    public void showWelcome() {
        showSeparator();
        showMessage(BANNER);
        showMessage("Hello! I'm Monkey, your cheeky little assistant.");
        showMessage("What can I do for you today?");
        showSeparator();
    }

    /** Returns the next command, or null when standard input is exhausted. */
    public String readCommand() {
        return scanner != null && scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /** Shows the boundary between command interactions. */
    public void showSeparator() {
        showMessage(SEPARATOR);
    }

    /** Shows a message to the user. */
    public void showMessage(String message) {
        output.accept(message);
    }
}
