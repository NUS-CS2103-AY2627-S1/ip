package goat;

import java.util.Scanner;

/**
 * Reads commands and displays all console messages.
 */
public class Ui {
    private static final String SEPARATOR =
            "____________________________________________________________";
    private static final String BANNER = "  ____             _\n"
            + " / ___| ___   __ _| |_\n"
            + "| |  _ / _ \\ / _` | __|\n"
            + "| |_| | (_) | (_| | |_\n"
            + " \\____|\\___/ \\__,_|\\__|";

    private final Scanner scanner = new Scanner(System.in);
    private String pendingLoadWarning;

    /**
     * Creates a console UI that reads from standard input.
     */
    public Ui() {
    }

    /**
     * Records a load warning to show after the welcome message.
     *
     * @param message storage error message
     */
    public void showLoadWarning(String message) {
        pendingLoadWarning = "I couldn't load saved tasks: " + message
                + " Starting with an empty task list.";
    }

    /**
     * Displays the banner, greeting, and any startup warning.
     */
    public void showWelcome() {
        showLine();
        System.out.println(BANNER);
        System.out.println("Hello! I'm Goat, your sure-footed task buddy.");
        System.out.println("Let's climb that task list together!");
        showLine();
        if (pendingLoadWarning != null) {
            showLine();
            showError(pendingLoadWarning);
            showLine();
        }
    }

    /**
     * Returns the greeting and any warning raised while loading saved tasks.
     *
     * @return startup message suitable for the GUI
     */
    public String getWelcomeMessage() {
        String message = "Hello! I'm Goat, your sure-footed task buddy.\n"
                + "Let's climb that task list together!";
        if (pendingLoadWarning != null) {
            message += "\n\nOOPS!!! " + pendingLoadWarning;
        }
        return message;
    }

    /**
     * Returns whether another command is available.
     *
     * @return {@code true} if another line can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads one command line.
     *
     * @return next command line
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the standard response separator.
     */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /**
     * Displays an input or storage error.
     *
     * @param message error details
     */
    public void showError(String message) {
        System.out.println("OOPS!!! " + message);
    }

    /**
     * Displays one complete response.
     *
     * @param response response to display
     */
    public void showResponse(String response) {
        System.out.println(response);
    }

    /**
     * Formats an input or storage error.
     *
     * @param message error details
     * @return formatted error
     */
    public String formatError(String message) {
        return "OOPS!!! " + message;
    }

    /**
     * Formats all tasks with one-based numbering.
     *
     * @param tasks tasks to format
     * @return formatted task list
     */
    public String formatTaskList(TaskList tasks) {
        return formatTasks("Here are the tasks in your list:", tasks);
    }

    /**
     * Formats tasks that matched a search keyword.
     *
     * @param tasks matching tasks
     * @return formatted matching tasks
     */
    public String formatMatchingTasks(TaskList tasks) {
        return formatTasks("Here are the matching tasks in your list:", tasks);
    }

    /**
     * Formats confirmation that tasks were arranged chronologically.
     *
     * @param tasks tasks in their new order
     * @return confirmation followed by the sorted task list
     */
    public String formatSortedTasks(TaskList tasks) {
        return formatTasks("All lined up! Dated tasks are chronological, "
                + "followed by undated tasks:", tasks);
    }

    /**
     * Formats a heading followed by one-based task entries.
     */
    private String formatTasks(String heading, TaskList tasks) {
        StringBuilder result = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            result.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return result.toString();
    }

    /**
     * Formats confirmation that a task was added.
     *
     * @param task added task
     * @param taskCount resulting task count
     * @return formatted confirmation
     */
    public String formatTaskAdded(Task task, int taskCount) {
        return "Hoof-tastic! I've added this task:\n  " + task
                + "\nNow you have " + formatTaskCount(taskCount) + " in the list.";
    }

    /**
     * Formats confirmation that a task was deleted.
     *
     * @param task deleted task
     * @param taskCount resulting task count
     * @return formatted confirmation
     */
    public String formatTaskDeleted(Task task, int taskCount) {
        return "One less climb! I've removed this task:\n  " + task
                + "\nNow you have " + formatTaskCount(taskCount) + " in the list.";
    }

    /**
     * Formats confirmation that a task's completion state changed.
     *
     * @param task changed task
     * @param isDone new completion state
     * @return formatted confirmation
     */
    public String formatCompletionChanged(Task task, boolean isDone) {
        if (isDone) {
            return "Baa-rilliant! I've marked this task as done:\n  " + task;
        }
        return "Back on the trail! I've marked this task as not done yet:\n  " + task;
    }

    /**
     * Returns the exit message.
     *
     * @return exit message
     */
    public String getGoodbyeMessage() {
        return "Time to hoof it. See you on the next climb!";
    }

    /**
     * Formats a task count using the correct singular or plural noun.
     */
    private String formatTaskCount(int taskCount) {
        return taskCount + (taskCount == 1 ? " task" : " tasks");
    }

}
