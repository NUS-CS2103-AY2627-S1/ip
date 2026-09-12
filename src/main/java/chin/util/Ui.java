package chin.util;

import java.util.Scanner;

/**
 * Handles all console I/O for the text-mode Chin loop. Reads commands via
 * a {@link Scanner} and prints framed messages to {@code stdout}.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private final Scanner scanner = new Scanner(System.in);

    /** Prints the initial greeting frame. */
    public void showWelcome() {
        System.out.println(LINE);
        System.out.println(" Hello! I'm Chin");
        System.out.println(" What can I do for you?");
        System.out.println(LINE);
    }

    /** Prints the farewell frame. */
    public void showGoodbye() {
        System.out.println(LINE);
        System.out.println(" Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    /** Prints the horizontal separator line. */
    public void showLine() {
        System.out.println(LINE);
    }

    /** Reads the next command line, returning {@code bye} on EOF. */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : "bye";
    }

    /** Prints a top-level message with one space of left padding. */
    public void show(String msg) {
        System.out.println(" " + msg);
    }

    /** Prints a nested/indented message. */
    public void showIndented(String msg) {
        System.out.println("   " + msg);
    }

    /** Prints an error message using the same style as {@link #show(String)}. */
    public void showError(String msg) {
        System.out.println(" " + msg);
    }
}
