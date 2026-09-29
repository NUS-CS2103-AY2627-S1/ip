package xue.ui;

import java.util.Scanner;

/** Handles console input and shared user-facing messages. */
public class Ui {
    /** Shared welcome text used by both console and GUI front ends. */
    public static final String WELCOME_MESSAGE = "Hello, I'm Xue — your reluctantly competent task assistant.\n"
            + "Give me a command and I will pretend it was my idea.";
    /** Divider printed between console interactions. */
    private static final String SEPARATOR = "________________________________________________________________________________";
    /** Reads commands entered by the user. */
    private final Scanner scanner = new Scanner(System.in);

    /** Displays Xue's welcome message. */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.println("██   ██  ██   ██  ███████\n"
                + " ██ ██   ██   ██  ██\n"
                + "  ███    ██   ██  █████\n"
                + " ██ ██   ██   ██  ██\n"
                + "██   ██   █████   ███████");
        System.out.println(WELCOME_MESSAGE);
    }

    /** Reads the next command, or returns null at end of input. */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /** Displays the standard divider. */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /** Displays an error message. */
    public void showError(String message) {
        System.out.println("OOPS!!! " + message);
    }
}
