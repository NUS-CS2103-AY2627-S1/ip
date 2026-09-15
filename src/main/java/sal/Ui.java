package sal;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles all interactions with the user: reading commands and printing messages.
 * Keeps {@code System.out}/{@code Scanner} usage out of the rest of the app.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = " ____        _ \n"
            + "/ ___|  __ _| |\n"
            + "\\___ \\ / _` | |\n"
            + " ___) | (_| | |\n"
            + "|____/ \\__,_|_|\n";

    private Scanner scanner;

    /**
     * Prints the welcome banner and greeting.
     */
    public void showWelcome() {
        showLine();
        System.out.println(BANNER);
        System.out.println(formatWelcome());
        showLine();
    }

    /**
     * Prints the goodbye message.
     */
    public void showGoodbye() {
        printBoxed(formatGoodbye());
    }

    /**
     * Prints a response between divider lines.
     *
     * @param message Text to show the user.
     */
    public void showMessage(String message) {
        printBoxed(message);
    }

    /**
     * Prints a horizontal divider line.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Reads the next command from the user and trims surrounding whitespace.
     *
     * @return The trimmed command line.
     */
    public String readCommand() {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        return scanner.nextLine().trim();
    }

    /**
     * Closes the input scanner when the app exits.
     */
    public void close() {
        if (scanner != null) {
            scanner.close();
            scanner = null;
        }
    }

    /**
     * Prints an error message between divider lines.
     *
     * @param message Error text to show the user.
     */
    public void showError(String message) {
        printBoxed(message);
    }

    /**
     * Tells the user that saved tasks could not be loaded.
     */
    public void showLoadingError() {
        showError("Could not load saved tasks. Starting with an empty list.");
    }

    /**
     * Prints the full task list.
     *
     * @param tasks Current tasks.
     */
    public void showTaskList(TaskList tasks) {
        printBoxed(formatTaskList(tasks));
    }

    /**
     * Prints tasks whose descriptions matched a find search.
     * Matching tasks are numbered from 1 in the order they appear in the list.
     *
     * @param matches Tasks to display.
     */
    public void showFoundTasks(ArrayList<Task> matches) {
        printBoxed(formatFoundTasks(matches));
    }

    /**
     * Confirms that a task was added.
     *
     * @param task Newly added task.
     * @param taskCount Current number of tasks.
     */
    public void showTaskAdded(Task task, int taskCount) {
        printBoxed(formatTaskAdded(task, taskCount));
    }

    /**
     * Confirms that a task was deleted.
     *
     * @param task Removed task.
     * @param taskCount Remaining number of tasks.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        printBoxed(formatTaskDeleted(task, taskCount));
    }

    /**
     * Confirms that a task was marked as done.
     *
     * @param task Updated task.
     */
    public void showMarked(Task task) {
        printBoxed(formatMarked(task));
    }

    /**
     * Confirms that a task was marked as not done.
     *
     * @param task Updated task.
     */
    public void showUnmarked(Task task) {
        printBoxed(formatUnmarked(task));
    }

    String formatWelcome() {
        return "Hello! I'm Sal.\nWhat can I do for you?";
    }

    String formatGoodbye() {
        return "Bye. Hope to see you again soon!";
    }

    String formatTaskList(TaskList tasks) {
        StringBuilder result = new StringBuilder("Here are the tasks in your list:");
        ArrayList<Task> list = tasks.getTasks();
        for (int i = 0; i < list.size(); i++) {
            result.append('\n').append(i + 1).append('.').append(list.get(i));
        }
        return result.toString();
    }

    String formatFoundTasks(ArrayList<Task> matches) {
        StringBuilder result = new StringBuilder("Here are the matching tasks in your list:");
        for (int i = 0; i < matches.size(); i++) {
            result.append('\n').append(i + 1).append('.').append(matches.get(i));
        }
        return result.toString();
    }

    String formatTaskAdded(Task task, int taskCount) {
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list.";
    }

    String formatTaskDeleted(Task task, int taskCount) {
        return "Noted. I've removed this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list.";
    }

    String formatMarked(Task task) {
        return "Nice! I've marked this task as done:\n  " + task;
    }

    String formatUnmarked(Task task) {
        return "OK, I've marked this task as not done yet:\n  " + task;
    }

    String formatTagged(Task task) {
        return "OK, I've tagged this task:\n  " + task;
    }

    private void printBoxed(String message) {
        showLine();
        System.out.println(message);
        showLine();
    }
}
