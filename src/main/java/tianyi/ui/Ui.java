package tianyi.ui;

import java.util.Scanner;

/**
 * Handles all console interactions with the user.
 */
public class Ui {
    private final Scanner scanner;

    /**
     * Creates a console interface that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Reports whether another command is available from standard input.
     *
     * @return {@code true} if another line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next complete command line.
     *
     * @return Command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the supplied application welcome message.
     *
     * @param message Welcome message to display.
     */
    public void showWelcome(String message) {
        showMessage(message);
    }

    /**
     * Displays a successful command response between separator lines.
     *
     * @param response Response to display.
     */
    public void showResponse(String response) {
        showMessage(response);
    }

    /**
     * Displays an application error between separator lines.
     *
     * @param message Error explanation to display.
     */
    public void showError(String message) {
        showMessage("Oops! " + message);
    }

    /**
     * Prints a horizontal separator line.
     */
    private void showLine() {
        System.out.println("____________________________________________________________");
    }

    /**
     * Displays one or more messages enclosed by separator lines.
     *
     * @param messages Messages to display in order.
     */
    private void showMessage(String... messages) {
        showLine();

        for (String message : messages) {
            System.out.println(message);
        }

        showLine();
    }
}
