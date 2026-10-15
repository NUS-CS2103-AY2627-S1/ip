package tblade.ui;

import java.util.Scanner;

import tblade.task.Task;
import tblade.task.TaskList;

/**
 * Deals with all interactions with the user. Response text is built by the {@code formatX}
 * methods, which are pure (no I/O) so that both the console UI and the GUI can share the exact
 * same wording; {@code showX} methods print that text to the console.
 */
public class Ui {
    private static final String PIG = " 🐷";
    private static final String SEPARATOR = "_".repeat(60);
    private static final String BANNER = "#####  ####   #       ###   ####   #####\n"
            + "  #    #   #  #      #   #  #   #  #\n"
            + "  #    ####   #      #####  #   #  ####\n"
            + "  #    #   #  #      #   #  #   #  #\n"
            + "  #    ####   #####  #   #  ####   #####";

    private final Scanner scanner;

    /**
     * Creates a Ui that reads user commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the startup banner and greeting.
     */
    public void showWelcome() {
        showLine();
        BANNER.lines().forEach(this::printLine);
        printLine("Yo, chat! I'm TBlade — Blood God of your task list.");
        printLine("What's the move?");
        showLine();
    }

    /**
     * Reads the next full line of user input.
     *
     * @return the command line entered by the user
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints a horizontal separator line.
     */
    public void showLine() {
        printLine(SEPARATOR);
    }

    /**
     * Prints a response, one console line per line of the given text.
     *
     * @param message text to display, built by one of the {@code formatX} methods
     */
    public void showMessage(String message) {
        message.lines().forEach(this::printLine);
    }

    /**
     * Builds the text shown for an error.
     *
     * @param message explanation of what went wrong and how to correct it
     * @return the formatted error text
     */
    public String formatError(String message) {
        return "Chat, is this real? " + message;
    }

    /**
     * Builds the farewell text shown when the user exits.
     *
     * @return the formatted farewell text
     */
    public String formatGoodbye() {
        return "gg. Technoblade never dies — I'll be right here when you're back.";
    }

    /**
     * Builds the text listing every task currently in the list, numbered from 1.
     *
     * @param tasks the tasks to display
     * @return the formatted task list text
     */
    public String formatTaskList(TaskList tasks) {
        return formatNumberedTasks("Here's your kill list:", tasks);
    }

    /**
     * Builds the text listing the tasks matching a search, numbered from 1.
     *
     * @param matches the matching tasks to display
     * @return the formatted matching-tasks text
     */
    public String formatMatchingTasks(TaskList matches) {
        return formatNumberedTasks("Matches found on the kill list:", matches);
    }

    /**
     * Builds the confirmation text shown after a task is added.
     *
     * @param task the task that was added
     * @param taskCount number of tasks now in the list
     * @return the formatted confirmation text
     */
    public String formatAddedTask(Task task, int taskCount) {
        return "Added to the kill list:\n"
                + "  " + task + "\n"
                + "Now you have " + taskCount + " tasks on the kill list.";
    }

    /**
     * Builds the confirmation text shown after a task is marked as done.
     *
     * @param task the task that was marked
     * @return the formatted confirmation text
     */
    public String formatMarked(Task task) {
        return "GG! Task conquered:\n"
                + "  [X] " + task.getDescription();
    }

    /**
     * Builds the confirmation text shown after a task is marked as not done.
     *
     * @param task the task that was unmarked
     * @return the formatted confirmation text
     */
    public String formatUnmarked(Task task) {
        return "Back in the fight — not done yet:\n"
                + "  [ ] " + task.getDescription();
    }

    /**
     * Builds the confirmation text shown after a task is deleted.
     *
     * @param task the task that was removed
     * @param taskCount number of tasks remaining in the list
     * @return the formatted confirmation text
     */
    public String formatDeleted(Task task, int taskCount) {
        return "Wiped from the kill list:\n"
                + "  " + task + "\n"
                + "Now you have " + taskCount + " tasks on the kill list.";
    }

    /**
     * Builds the text listing a task list under the given header, numbered from 1.
     */
    private String formatNumberedTasks(String header, TaskList tasks) {
        StringBuilder text = new StringBuilder(header);
        for (int i = 0; i < tasks.size(); i++) {
            text.append('\n').append(i + 1).append('.').append(tasks.get(i));
        }
        return text.toString();
    }

    /**
     * Prints one application output line with a pig emoji at its end.
     *
     * @param text text to display before the emoji
     */
    private void printLine(String text) {
        System.out.println(text + PIG);
    }
}
