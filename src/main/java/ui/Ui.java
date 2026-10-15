package ui;

import java.util.Scanner;

import command.CommandResult;

/** Handles CBT's console input and common output formatting. */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private final Scanner scanner = new Scanner(System.in);

    /** Prints the application's greeting. */
    public void showWelcome() {
        String banner = "       .       *\n"
                + "     C B T\n"
                + " *       .       *\n";
        printLines(DIVIDER, banner, "Mission control online. I'm CBT.",
                "Where shall we steer your day?", DIVIDER);
    }

    /**
     * Returns whether another command is available from standard input.
     *
     * @return {@code true} if another input line is available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command.
     *
     * @return next command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Prints a user-correctable error message.
     *
     * @param message error message to display.
     */
    public void showError(String message) {
        printLines(message);
    }

    /** Prints a horizontal divider. */
    public void showLine() {
        printLines(DIVIDER);
    }

    /**
     * Prints the result message produced by a command.
     *
     * @param result command result to display.
     */
    public void showResult(CommandResult result) {
        if (!result.message().isEmpty()) {
            printLines(result.message());
        }
    }

    /** Prints each supplied line in its given order. */
    private void printLines(String... lines) {
        for (String line : lines) {
            System.out.println(line);
        }
    }
}
