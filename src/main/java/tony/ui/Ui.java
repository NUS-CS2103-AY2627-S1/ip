package tony.ui;

import java.util.Scanner;

/**
 * Handles all console input and output for the Tony chatbot.
 */
public class Ui {
    /** Tony's name banner displayed at startup. */
    private static final String BANNER = " _____   ___   _   _ __   __\n"
            + "|_   _| / _ \\ | \\ | |\\ \\ / /\n"
            + "  | |  | | | ||  \\| | \\ V /\n"
            + "  | |  | |_| || |\\  |  | |\n"
            + "  |_|   \\___/ |_| \\_|  |_|";

    /** Divider used to separate commands and responses. */
    private static final String LINE = "________________________________________________";

    /** Reads commands entered through standard input. */
    private final Scanner scanner;

    /** Creates a console UI that reads from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays Tony's startup banner and greeting. */
    public void showWelcome() {
        System.out.println(BANNER);
        showLine();
        System.out.println("Good day, Chief. What shall I arrange for you?");
        showLine();
    }

    /** Displays the divider line. */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Returns whether another complete command can be read.
     *
     * @return whether standard input contains another line.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the complete command line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays a complete chatbot response.
     *
     * @param message response to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }
}
