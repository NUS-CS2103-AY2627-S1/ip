package shrek.ui;

import java.util.Scanner;

/**
 * Deals with interactions with the user: reading input and printing output.
 */
public class Ui {
    private static final String SEPARATOR = "    ____________________________________________________________";
    private static final String BANNER = "     ____  _              _    \n"
            + "    / ___|| |__  _ __ ___| | __\n"
            + "    \\___ \\| '_ \\| '__/ _ \\ |/ /\n"
            + "     ___) | | | | | |  __/   < \n"
            + "    |____/|_| |_|_|  \\___|_|\\_\\\n";

    private final Scanner scanner;

    /**
     * Creates a new Ui with a Scanner reading from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prints the welcome banner and greeting.
     */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.println(BANNER);
        System.out.println("     Hello! I'm Shrek.");
        System.out.println("     What can I do for you?");
        System.out.println(SEPARATOR);
    }

    /**
     * Prints a warning that saved tasks could not be loaded.
     */
    public void showLoadingError() {
        System.out.println("     Warning: could not load saved tasks. Starting with an empty list.");
        System.out.println(SEPARATOR);
    }

    /**
     * Prints a response produced by the chatbot command processor.
     *
     * @param response the response to print.
     */
    public void showResponse(String response) {
        System.out.println("     " + response.replace("\n", "\n     "));
        System.out.println(SEPARATOR);
    }

    /**
     * Reads one line of user input.
     *
     * @return the raw input line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Closes the input scanner.
     */
    public void close() {
        scanner.close();
    }

}
