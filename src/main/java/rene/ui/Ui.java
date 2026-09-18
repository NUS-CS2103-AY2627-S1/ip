package rene.ui;

import java.util.List;
import java.util.Scanner;

import rene.task.Task;

/**
 * Handles all console input and output for Rene.
 * Rene speaks in a measured, corporate register: deliverables, action items,
 * and touch bases, not chit-chat.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String GREETING = "Good day. Rene here, your personal productivity liaison.\n"
            + "Let's align on your priorities for the day.";
    private static final String GOODBYE = "Thank you for your time. Standing by — let's touch base again soon.";
    private static final String HELP =
            " Per your request, here is the standard operating procedure for working with Rene:\n"
            + " todo DESCRIPTION\n"
            + " deadline DESCRIPTION /by yyyy-MM-dd\n"
            + " event DESCRIPTION /from START /to END\n"
            + " list\n"
            + " mark NUMBER\n"
            + " unmark NUMBER\n"
            + " delete NUMBER\n"
            + " find KEYWORD\n"
            + " help\n"
            + " bye";
    private static final String BANNER = " ____\n"
            + "|  _ \\ ___ _ __   ___\n"
            + "| |_) / _ \\ '_ \\ / _ \\\n"
            + "|  _ <  __/ | | |  __/\n"
            + "|_| \\_\\___|_| |_|\\___|";

    private final Scanner scanner;

    /**
     * Creates a console UI that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return {@code true} if another line can be read.
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
     * Displays Rene's greeting and any problem encountered while loading tasks.
     *
     * @param loadingError the loading error to display, or {@code null} when loading succeeded.
     */
    public void showWelcome(String loadingError) {
        showDivider();
        System.out.println(BANNER);
        System.out.println(GREETING);
        showDivider();
        if (loadingError != null) {
            showError(loadingError);
            showDivider();
        }
    }

    /**
     * Displays the farewell message.
     */
    public void showGoodbye() {
        System.out.println(formatGoodbye());
    }

    /**
     * Displays a user-friendly command or storage error.
     *
     * @param message the explanation to display.
     */
    public void showError(String message) {
        System.out.println(formatError(message));
    }

    /**
     * Displays a response formatted by this UI.
     *
     * @param response the complete response to display.
     */
    public void showResponse(String response) {
        System.out.println(response);
    }

    /**
     * Formats Rene's greeting and any problem encountered while loading tasks.
     *
     * @param loadingError the loading error, or {@code null} when loading succeeded.
     * @return the greeting suitable for a graphical UI.
     */
    public String formatWelcome(String loadingError) {
        if (loadingError == null) {
            return GREETING;
        }
        return GREETING + "\n\n" + formatError(loadingError).trim();
    }

    /**
     * Formats the farewell response.
     *
     * @return the farewell response.
     */
    public String formatGoodbye() {
        return GOODBYE;
    }

    /**
     * Formats guidance for every command supported by Rene.
     *
     * @return the command syntax reference.
     */
    public String formatHelp() {
        return HELP;
    }

    /**
     * Formats a user-friendly command or storage error.
     *
     * @param message the explanation to include.
     * @return the formatted error response.
     */
    public String formatError(String message) {
        return " Apologies — " + message;
    }

    /**
     * Formats confirmation that a task was added.
     *
     * @param task the added task.
     * @param taskCount the resulting task count.
     * @return the formatted confirmation.
     */
    public String formatTaskAdded(Task task, int taskCount) {
        return formatLines(
                " Noted. I've logged this deliverable on your action items:",
                "   " + task,
                formatTaskCount(taskCount));
    }

    /**
     * Formats all supplied tasks with one-based numbers.
     *
     * @param tasks the tasks to include.
     * @return the formatted task list.
     */
    public String formatTasks(List<Task> tasks) {
        return formatNumberedTasks(" Here is your current action-item backlog:", tasks);
    }

    /**
     * Formats tasks that match a find command with one-based result numbers.
     *
     * @param tasks the matching tasks to include.
     * @return the formatted matching task list.
     */
    public String formatMatchingTasks(List<Task> tasks) {
        return formatNumberedTasks(" Here are the action items matching your search criteria:", tasks);
    }

    /**
     * Formats confirmation that a task was marked as completed.
     *
     * @param task the updated task.
     * @return the formatted confirmation.
     */
    public String formatTaskMarked(Task task) {
        return formatLines(
                " Well received. I've marked this item as actioned:",
                "   " + task);
    }

    /**
     * Formats confirmation that a task was marked as incomplete.
     *
     * @param task the updated task.
     * @return the formatted confirmation.
     */
    public String formatTaskUnmarked(Task task) {
        return formatLines(
                " Understood. This item has been reopened:",
                "   " + task);
    }

    /**
     * Formats confirmation that a task was removed.
     *
     * @param task the removed task.
     * @param taskCount the resulting task count.
     * @param tasksWereRenumbered whether any tasks remain and received new display numbers.
     * @return the formatted confirmation.
     */
    public String formatTaskDeleted(Task task, int taskCount, boolean tasksWereRenumbered) {
        String response = formatLines(
                " Noted. This action item has been removed from the pipeline:",
                "   " + task,
                formatTaskCount(taskCount));
        if (tasksWereRenumbered) {
            response = formatLines(response, " The remaining action items have been renumbered.");
        }
        return response;
    }

    /**
     * Displays the divider used to separate console interactions.
     */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /**
     * Displays a task count with grammatically correct wording.
     */
    private String formatTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "action item" : "action items";
        return " Your backlog now contains " + taskCount + " " + taskWord + ".";
    }

    /**
     * Joins any number of response lines using the platform-independent newline used by the UI contract.
     */
    private String formatLines(String... lines) {
        return String.join("\n", lines);
    }

    /**
     * Formats supplied tasks with a heading and one-based numbers.
     */
    private String formatNumberedTasks(String heading, List<Task> tasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int index = 0; index < tasks.size(); index++) {
            response.append('\n')
                    .append(' ')
                    .append(index + 1)
                    .append('.')
                    .append(tasks.get(index));
        }
        return response.toString();
    }
}
