package lily.ui;

import java.util.NoSuchElementException;
import java.util.Scanner;

/** Handles all console input and output for Lily. */
public class Ui {
    private static final String BANNER = " _     _ _       \n"
            + "| |   (_) |      \n"
            + "| |    _| |_   _ \n"
            + "| |   | | | | | |\n"
            + "| |___| | | |_| |\n"
            + "\\_____/_|\\__, |  \n"
            + "          __/ |  \n"
            + "         |___/   \n";
    private static final String DIVIDER = "----------------------------------------------------------";

    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Shows Lily's welcome banner and supplied initial prompt. */
    public void showWelcome(String welcomeMessage) {
        System.out.println(BANNER);
        System.out.println(welcomeMessage);
        showDivider();
    }

    /**
     * Reads and trims one command, or returns {@code null} when input has ended.
     */
    public String readCommand() {
        try {
            return scanner.nextLine().trim();
        } catch (NoSuchElementException | IllegalStateException e) {
            return null;
        }
    }

    /** Shows a separator after a command result. */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /** Shows the reply returned by Lily's shared command processor. */
    public void showResponse(String response) {
        System.out.println(response);
        showDivider();
    }
}
