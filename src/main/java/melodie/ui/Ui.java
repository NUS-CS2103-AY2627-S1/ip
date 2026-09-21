package melodie.ui;

import java.util.Scanner;

import melodie.Melodie;

/**
 * Handles console input and output for Melodie.
 */
public class Ui {
    private static final String BANNER =
            " __  __      _           _ _\n"
            + "|  \\/  | ___| | ___   __| (_) ___\n"
            + "| |\\/| |/ _ \\ |/ _ \\ / _` | |/ _ \\\n"
            + "| |  | |  __/ | (_) | (_| | |  __/\n"
            + "|_|  |_|\\___|_|\\___/ \\__,_|_|\\___|\n"
            + "\n"
            + "          ♪  ♫  ♪";
    private final Scanner scanner;

    /**
     * Creates a user interface that reads from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads and trims the next command entered by the user.
     *
     * @return Command entered by the user without surrounding whitespace.
     */
    public String readCommand() {
        return this.scanner.nextLine().trim();
    }

    /**
     * Displays Melodie's banner and greeting.
     */
    public void showIntro() {
        System.out.println("____________________________________________________________\n"
                + BANNER + "\n"
                + Melodie.GREETING_MESSAGE + "\n"
                + "____________________________________________________________\n");
    }

    /**
     * Displays a line separating sections of output.
     */
    public void showLine() {
        System.out.println("    ____________________________________________________________");
    }

    /**
     * Displays a response with console indentation preserved on every line.
     *
     * @param response Response to display.
     */
    public void showResponse(String response) {
        System.out.println("    " + response.replace("\n", "\n    "));
    }

    /**
     * Closes the input scanner used by the user interface.
     */
    public void close() {
        this.scanner.close();
    }

}
