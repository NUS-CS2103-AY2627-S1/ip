package jasper.ui;

import java.util.Scanner;

import jasper.command.CommandResult;

/**
 * Handles the user interface interactions including reading input and displaying output.
 */
public class Ui {
    /** Separator line used to divide UI segments */
    private static final String SEPARATOR = "-".repeat(60).indent(4);
    /** ANSI escape code for red text */
    private static final String ANSI_RED = "\u001B[31m";
    /** ANSI escape code to reset text formatting */
    private static final String ANSI_RESET = "\u001B[0m";

    /** Scanner used to read user input from standard input */
    private final Scanner scanner;

    /**
     * Constructs a UI instance and initializes the scanner.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Closes the scanner to release system resources.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Reads the next command line from the user.
     */
    public String readCommand() {
        if (!scanner.hasNextLine()) {
            System.out.println();
            return "bye";
        }
        String input = scanner.nextLine();
        assert input != null : "User text input string is missing";
        return input.strip();
    }

    /**
     * Prints the predefined separator line to the console.
     */
    public void showLine() {
        System.out.print(SEPARATOR);
    }

    /**
     * Displays a formatted response to the user based on their command's result.
     */
    public void showResponse(CommandResult result) {
        switch (result.commandType()) {
            case ERROR -> showError(result.response());
            default -> System.out.print(result.response().indent(4));
        }
    }

    /**
     * Displays an error message to user.
     */
    public void showError(String err) {
        String output = ANSI_RED + "Something is amiss... " + ANSI_RESET + err;
        System.out.println(output.indent(4));
    }

    /**
     * Displays the welcome message and a list of supported commands on startup.
     */
    public void showWelcome() {
        showLine();
        System.out.print("""
                    _                                \s
                 _ | |  __ _   ___  _ __   ___   _ _ \s
                | || | / _` | (_-< | '_ \\ / -_) | '_|\s
                 \\__/  \\__,_| /__/ | .__/ \\___| |_|  \s
                                   |_|               \s
                """.indent(4)
        );
        System.out.print("""
                Hello! I'm Jasper. Whatever shalt thou require of me today?
                Supported commands:
                    bye, list, find, unmark, mark, delete, todo, deadline, event
                """.indent(4)
        );
        showLine();
    }
}
