package chatty.ui;

import java.util.List;
import java.util.Scanner;

import chatty.task.RecurringTask;
import chatty.task.Task;

/**
 * Handles console input and presents Chatty's responses to the user.
 */
public class Ui {
    /** Line used to separate Chatty's responses from user input. */
    private static final String HORIZONTAL_LINE = "____________________________________________________________";

    /** Chatty's logo, displayed when the application starts. */
    private static final String BANNER = "  ____ _           _   _         \n"
            + " / ___| |__   __ _| |_| |_ _   _ \n"
            + "| |   | '_ \\ / _` | __| __| | | |\n"
            + "| |___| | | | (_| | |_| |_| |_| |\n"
            + " \\____|_| |_|\\__,_|\\__|\\__|\\__, |\n"
            + "                           |___/";

    /** Greeting displayed after Chatty's logo. */
    private static final String WELCOME = "Chatty online.\nWhat shall we put on today's flight plan?";

    /** Scanner used to read commands from standard input. */
    private final Scanner scanner;

    /** Creates a user interface that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another command is available.
     *
     * @return {@code true} when another command line is available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Returns the next command entered by the user.
     *
     * @return next command line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays Chatty's startup banner and greeting. */
    public void showGreeting() {
        printLines(HORIZONTAL_LINE, BANNER, WELCOME, HORIZONTAL_LINE);
    }

    /** Displays the line that separates responses from user input. */
    public void showLine() {
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays a response produced by Chatty.
     *
     * @param response response to display.
     */
    public void showResponse(String response) {
        System.out.println(response);
    }

    /**
     * Returns Chatty's farewell response.
     *
     * @return farewell response.
     */
    public String formatExit() {
        return " Chatty signing off. Keep your next milestone in sight!";
    }

    /**
     * Formats an error message for display.
     *
     * @param message error message to format.
     * @return formatted error response.
     */
    public String formatError(String message) {
        return " " + message;
    }

    /**
     * Formats every task with its one-based list number.
     *
     * @param tasks tasks to format.
     * @return formatted task-list response.
     */
    public String formatTaskList(List<Task> tasks) {
        return formatTasks(" Flight plan status:", tasks);
    }

    /**
     * Formats matching tasks using their original one-based task-list numbers.
     *
     * @param matchingTasks matching task instances from the full task list.
     * @param allTasks full task list used by mark, unmark, and delete.
     * @return formatted matching-task response with actionable task numbers.
     */
    public String formatMatchingTasks(List<Task> matchingTasks, List<Task> allTasks) {
        StringBuilder response = new StringBuilder(" Radar found these matching tasks:");
        for (int i = 0; i < allTasks.size(); i++) {
            if (matchingTasks.contains(allTasks.get(i))) {
                response.append('\n').append(' ').append(i + 1).append('.').append(allTasks.get(i));
            }
        }
        return response.toString();
    }

    /**
     * Formats confirmation that a task was added.
     *
     * @param task task that was added.
     * @param taskCount number of tasks after the addition.
     * @return formatted task-added response.
     */
    public String formatTaskAdded(Task task, int taskCount) {
        return formatLines(
                " Task added to the flight plan:",
                "   " + task,
                " " + taskCount + " task(s) now on board.");
    }

    /**
     * Formats confirmation that a task was marked as done.
     *
     * @param task task that was marked as done.
     * @return formatted task-marked response.
     */
    public String formatTaskMarked(Task task) {
        return formatLines(" Milestone cleared:", "   " + task);
    }

    /**
     * Formats confirmation that a recurring occurrence was completed and advanced.
     *
     * @param task recurring task after its schedule was advanced.
     * @return formatted recurring-task response.
     */
    public String formatRecurringTaskAdvanced(RecurringTask task) {
        return formatLines(
                " Recurring milestone cleared. Next occurrence locked in:",
                "   " + task);
    }

    /**
     * Formats confirmation that a task was marked as not done.
     *
     * @param task task that was marked as not done.
     * @return formatted task-unmarked response.
     */
    public String formatTaskUnmarked(Task task) {
        return formatLines(" Task returned to active duty:", "   " + task);
    }

    /**
     * Formats confirmation that a task was deleted.
     *
     * @param task task that was deleted.
     * @param taskCount number of tasks after the deletion.
     * @return formatted task-deleted response.
     */
    public String formatTaskDeleted(Task task, int taskCount) {
        return formatLines(
                " Task removed from the flight plan:",
                "   " + task,
                " " + taskCount + " task(s) remain on board.");
    }

    /**
     * Formats a task collection below the supplied heading.
     *
     * @param heading heading shown above the tasks.
     * @param tasks tasks to format.
     * @return formatted task collection.
     */
    private String formatTasks(String heading, List<Task> tasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            response.append('\n')
                    .append(' ')
                    .append(i + 1)
                    .append('.')
                    .append(tasks.get(i));
        }
        return response.toString();
    }

    /**
     * Displays each supplied line in order.
     *
     * @param lines response lines to display.
     */
    private void printLines(String... lines) {
        System.out.println(formatLines(lines));
    }

    /**
     * Joins a variable number of response lines into one response.
     *
     * @param lines response lines to join.
     * @return supplied lines separated by newline characters.
     */
    private String formatLines(String... lines) {
        return String.join("\n", lines);
    }
}
