package amy;

import java.util.Scanner;

/**
 * Handles Amy's interaction with the user through the console.
 */
public class Ui {
    private static final String SEPARATOR = "_".repeat(80);
    private static final String BANNER = "  A     m   m  y   y\n"
            + " A A    mm mm   y y\n"
            + "AAAAA   m m m    y\n"
            + "A   A   m   m    y\n"
            + "A   A   m   m    y\n";

    private final Scanner scanner;

    /**
     * Creates a console UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Amy's welcome message.
     */
    public void showWelcome() {
        showSeparator();
        System.out.print(BANNER);
        System.out.println("Hello! I'm Amy.");
        System.out.println("What can I do for you?");
        showSeparator();
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return true when another input line is available
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command from the user.
     *
     * @return the next input line
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the separator between interactions.
     */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /**
     * Displays Amy's farewell message.
     */
    public void showFarewell() {
        System.out.println("Lovely to see you! Until next time.");
        showSeparator();
    }

    /**
     * Displays a message to the user.
     *
     * @param message the message to display
     */
    public void showMessage(String message) {
        System.out.println(message);
    }
}
