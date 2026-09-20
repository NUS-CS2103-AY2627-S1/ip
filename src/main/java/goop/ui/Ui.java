package goop.ui;

import java.util.List;
import java.util.Scanner;

import goop.task.Priority;
import goop.task.Task;
import goop.task.TaskList;

/**
 * Handles all console input and output for the chatbot.
 */
public class Ui {
    private static final String DIVIDER =
            "____________________________________________________________";
    private static final String BANNER = "  ____\n"
            + " / ___| ___   ___  _ __\n"
            + "| |  _ / _ \\ / _ \\| '_ \\\n"
            + "| |_| | (_) | (_) | |_) |\n"
            + " \\____|\\___/ \\___/| .__/\n"
            + "                  |_|\n";

    private final Scanner scanner;

    /**
     * Creates a UI connected to standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Checks whether another line of user input is available.
     *
     * @return True when another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads one command and removes surrounding whitespace.
     *
     * @return Trimmed user command.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays the startup banner and greeting.
     */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.print(BANNER);
        System.out.println(" Hello! I'm Goop.");
        System.out.println(" What can I do for you?");
        System.out.println(DIVIDER);
    }

    /**
     * Displays the separator shown before each command response.
     */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /**
     * Warns that saved data could not be loaded and an empty list will be used.
     *
     * @param message Explanation supplied by storage.
     */
    public void showLoadingError(String message) {
        showResponse("WARNING: " + message
                + "\nStarting with an empty task list.");
    }

    /**
     * Displays every task with its one-based list number.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        showNumberedTasks("Here are the tasks in your list:", tasks.getTasks());
    }

    /**
     * Displays tasks that matched a find command, numbered within the results.
     *
     * @param matchingTasks Tasks whose descriptions contain the search keyword.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        showNumberedTasks("Here are the matching tasks in your list:", matchingTasks);
    }

    /**
     * Formats an ordered task list with consecutive one-based display numbers.
     */
    private void showNumberedTasks(String heading, List<Task> tasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            response.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        showResponse(response.toString());
    }

    /**
     * Confirms that a task was deleted.
     *
     * @param task Deleted task.
     * @param taskCount Number of remaining tasks.
     */
    public void showDeletedTask(Task task, int taskCount) {
        showResponse("Noted. I've removed this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list.");
    }

    /**
     * Confirms that a task was marked as incomplete.
     *
     * @param task Updated task.
     */
    public void showUnmarkedTask(Task task) {
        showResponse("OK, I've marked this task as not done yet:\n  " + task);
    }

    /**
     * Confirms that a task was marked as complete.
     *
     * @param task Updated task.
     */
    public void showMarkedTask(Task task) {
        showResponse("Nice! I've marked this task as done:\n  " + task);
    }

    /**
     * Confirms that a task was added.
     *
     * @param task Added task.
     * @param taskCount Current task count.
     */
    public void showAddedTask(Task task, int taskCount) {
        showResponse("Got it. I've added this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list.");
    }

    /**
     * Confirms a saved priority change or removal.
     *
     * @param task Updated task.
     */
    public void showPriorityChanged(Task task) {
        String message = task.getPriority() == Priority.NONE
                ? "OK, I've cleared this task's priority:"
                : "OK, I've set this task's priority to " + task.getPriority().getLabel() + ":";
        showResponse(message + "\n  " + task);
    }

    /**
     * Displays a recoverable command or storage error.
     *
     * @param message Explanation of the error.
     */
    public void showError(String message) {
        showResponse("ERROR: " + message);
    }

    /**
     * Displays the farewell message.
     */
    public void showGoodbye() {
        showResponse("Bye. Hope to see you again soon!");
    }

    /**
     * Displays a command response followed by the console divider.
     * Subclasses can override this method to send responses elsewhere.
     *
     * @param message Response without console-specific indentation.
     */
    protected void showResponse(String message) {
        System.out.println(" " + message.replace("\n", "\n "));
        System.out.println(DIVIDER);
    }
}
