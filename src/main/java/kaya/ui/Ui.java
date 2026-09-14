package kaya.ui;

import java.util.List;
import java.util.Scanner;

import kaya.task.Task;

/**
 * Handles all console input and output for Kaya.
 */
public class Ui {
    private static final String DIVIDER =
            "____________________________________________________________";
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Creates a user interface connected to standard input and output.
     */
    public Ui() {
        // Scanner is initialized when the UI is created.
    }

    /**
     * Returns whether another command can be read.
     *
     * @return {@code true} if another input line is available
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command.
     *
     * @return the next command entered by the user
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays Kaya's startup greeting.
     */
    public void showGreeting() {
        showLine();
        showBanner();
        showMessages(
                Messages.GREETING,
                "Try: todo read a book, list, or bye.");
        showLine();
    }

    /**
     * Displays each supplied message on its own line.
     *
     * @param messages the messages to display
     */
    private void showMessages(String... messages) {
        for (String message : messages) {
            showMessage(message);
        }
    }

    /**
     * Displays a message to the user.
     *
     * @param message the message to display
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays an error in Kaya's standard format.
     *
     * @param message the error details to display
     */
    public void showError(String message) {
        showMessage("Sorry, " + message);
    }

    /**
     * Displays every task with a one-based number.
     *
     * @param tasks the tasks to display
     */
    public void showTasks(List<Task> tasks) {
        showMessage(Messages.formatTasks(Messages.TASKS_HEADING, Messages.EMPTY_LIST, tasks));
    }

    /**
     * Displays tasks matching a search keyword with one-based numbers.
     *
     * @param tasks the matching tasks to display
     */
    public void showMatchingTasks(List<Task> tasks) {
        showMessage(Messages.formatTasks(Messages.MATCHES_HEADING, Messages.NO_MATCHES, tasks));
    }

    /**
     * Displays the divider used between commands.
     */
    public void showLine() {
        showMessage(DIVIDER);
    }

    /**
     * Displays Kaya's text banner.
     */
    private void showBanner() {
        String banner = "  _  __            _  _          \n"
                        + " | |/ /   __ _    | || |  __ _   \n"
                        + " | ' <   / _` |    \\_, | / _` |  \n"
                        + " |_|\\_\\  \\__,_|   _|__/  \\__,_|  \n"
                        + "_|\"\"\"\"\"|_|\"\"\"\"\"|_| \"\"\"\"|_|\"\"\"\"\"| \n"
                        + "\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'";
        showMessage(banner);
    }

    /**
     * Closes the console input resource.
     */
    public void close() {
        scanner.close();
    }
}
