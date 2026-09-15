package larp;

import java.util.Scanner;

/**
 * Handles console input and output for Larp.
 */
public class Ui {
    static final String ERROR_PREFIX = "Let's fix that: ";
    private static final String BANNER = " _        _    ____  ____\n"
            + "| |      / \\  |  _ \\|  _ \\\n"
            + "| |     / _ \\ | |_) | |_) |\n"
            + "| |___ / ___ \\|  _ <|  __/\n"
            + "|_____/_/   \\_\\_| \\_\\_|\n";

    private final Scanner scanner;

    /**
     * Creates a console user interface that reads from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the application banner and greeting.
     */
    public void showWelcome() {
        System.out.println(BANNER);
        System.out.println("Hello, I'm Larp.");
        System.out.println("Ready when you are. Add a task, check your list, or find something.");
    }

    /**
     * Displays one complete response.
     *
     * @param message Response to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Returns whether another command is available from standard input.
     *
     * @return {@code true} if another command can be read, or {@code false} otherwise.
     */
    public boolean hasNextCommand() {
        return this.scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command from standard input.
     *
     * @return Trimmed user command.
     */
    public String readCommand() {
        return this.scanner.nextLine().trim();
    }

    /**
     * Returns the farewell message.
     *
     * @return Farewell message.
     */
    public String getGoodbyeMessage() {
        return "All set. Your tasks are saved. See you next time!";
    }

    /**
     * Formats every task with a one-based list number.
     *
     * @param tasks Tasks to include.
     * @return Message containing every task.
     */
    public String getTaskListMessage(TaskList tasks) {
        if (tasks.isEmpty()) {
            return "Your task list is empty.";
        }

        return "Here are the tasks in your list:\n" + getNumberedTasks(tasks);
    }

    /**
     * Formats tasks that match a find command.
     *
     * @param tasks Matching tasks to include.
     * @return Message containing the matching tasks.
     */
    public String getMatchingTasksMessage(TaskList tasks) {
        if (tasks.isEmpty()) {
            return "No matching tasks found.";
        }

        return "Here are the matching tasks in your list:\n" + getNumberedTasks(tasks);
    }

    private String getNumberedTasks(TaskList tasks) {
        StringBuilder numberedTasks = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            if (i > 0) {
                numberedTasks.append(System.lineSeparator());
            }
            numberedTasks.append(i + 1).append('.').append(tasks.get(i));
        }
        return numberedTasks.toString();
    }

    /**
     * Formats confirmation that a task was marked as completed.
     *
     * @param task Updated task.
     * @return Confirmation message.
     */
    public String getMarkedTaskMessage(Task task) {
        return "Done. I marked this task as complete:\n  " + task;
    }

    /**
     * Formats confirmation that a task was marked as incomplete.
     *
     * @param task Updated task.
     * @return Confirmation message.
     */
    public String getUnmarkedTaskMessage(Task task) {
        return "Reopened. This task is back on your list:\n  " + task;
    }

    /**
     * Formats a removed task and the remaining task count.
     *
     * @param task Removed task.
     * @param taskCount Number of tasks remaining.
     * @return Confirmation message.
     */
    public String getDeletedTaskMessage(Task task, int taskCount) {
        return "Removed this task:\n  " + task
                + "\nNow you have " + taskCount + " " + getTaskNoun(taskCount) + " in the list.";
    }

    /**
     * Formats an added task and the updated task count.
     *
     * @param task Added task.
     * @param taskCount Updated number of tasks.
     * @return Confirmation message.
     */
    public String getAddedTaskMessage(Task task, int taskCount) {
        return "Captured this task:\n  " + task
                + "\nNow you have " + taskCount + " " + getTaskNoun(taskCount) + " in the list.";
    }

    /**
     * Formats an error message for the user.
     *
     * @param message Explanation of the error.
     * @return Formatted error message.
     */
    public String getErrorMessage(String message) {
        return ERROR_PREFIX + message;
    }

    /**
     * Returns whether a response is a formatted error message.
     *
     * @param message Response to inspect.
     * @return {@code true} if the response should use the error presentation.
     */
    static boolean isErrorMessage(String message) {
        return message.startsWith(ERROR_PREFIX);
    }

    private String getTaskNoun(int taskCount) {
        return taskCount == 1 ? "task" : "tasks";
    }
}
