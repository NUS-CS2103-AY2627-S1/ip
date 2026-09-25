package shan.ui;

import java.util.Scanner;

import shan.ResponseSink;

/**
 * Handles console input and output for Shan.
 */
public class Ui implements ResponseSink {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER = " ____  _\n"
            + "/ ___|| |__   __ _ _ __\n"
            + "\\___ \\| '_ \\ / _` | '_ \\\n"
            + " ___) | | | | (_| | | | |\n"
            + "|____/|_| |_|\\__,_|_| |_| \n";

    private final Scanner scanner;

    /**
     * Constructs a console UI that reads from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays Shan's banner and greeting.
     */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println("Hey! I'm Shan.\nHow can I help?");
        System.out.println(DIVIDER);
    }

    /**
     * Returns whether another command is available from standard input.
     *
     * @return {@code true} when another command can be read.
     */
    public boolean hasNextCommand() {
        return this.scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command from standard input.
     *
     * @return Next user command.
     */
    public String readCommand() {
        return this.scanner.nextLine().trim();
    }

    /**
     * Displays a message using Shan's standard response formatting.
     *
     * @param message Message to display.
     */
    @Override
    public void showMessage(String message) {
        System.out.println(DIVIDER);
        System.out.println("Shan: " + message);
        System.out.println(DIVIDER);
    }

    /**
     * Closes the console input scanner.
     */
    public void close() {
        this.scanner.close();
    }
}
