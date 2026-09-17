package sace;

import java.util.Scanner;

/**
 * Handles console input and displays Sace's responses to the user.
 */
public class Ui {
    private static final String HORIZONTAL_LINE =
            "____________________________________________________________";
    private static final String BANNER = "  ____      _      ____   _____\n"
            + " / ___|    / \\    / ___| | ____|\n"
            + " \\___ \\   / _ \\  | |     |  _|\n"
            + "  ___) | / ___ \\ | |___  | |___\n"
            + " |____/ /_/   \\_\\ \\____| |_____|\n";

    private final Scanner scanner;

    /**
     * Creates a console user interface reading from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Sace's banner and greeting.
     */
    public void showWelcome(String welcomeMessage) {
        System.out.println(HORIZONTAL_LINE);
        System.out.print(BANNER);
        System.out.println(welcomeMessage);
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Returns whether another command is available from standard input.
     *
     * @return {@code true} when another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command.
     *
     * @return command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays a line separating the user's command from Sace's response.
     */
    public void showDivider() {
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays a response followed by the standard divider.
     *
     * @param response response to display.
     */
    public void showResponse(String response) {
        System.out.println(response);
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Releases the console input scanner.
     */
    public void close() {
        scanner.close();
    }
}
