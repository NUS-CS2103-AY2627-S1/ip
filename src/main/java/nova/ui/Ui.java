package nova.ui;

import java.util.Scanner;

import nova.task.Task;
import nova.task.TaskList;

/**
 * Handles console input and formats user-facing messages for Nova.
 */
public class Ui {
    private static final String HORIZONTAL_LINE =
            "____________________________________________________________";

    private final Scanner scanner;

    /**
     * Creates a console user interface.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another command is available from standard input.
     *
     * @return True if another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Returns the next command from standard input.
     *
     * @return Next command.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints a response to the console.
     *
     * @param response Response to print.
     */
    public void showResponse(String response) {
        System.out.println(response);
    }

    /**
     * Prints a formatted error message.
     *
     * @param message Explanation of the error.
     */
    public void showError(String message) {
        showResponse(getErrorMessage(message));
    }

    /**
     * Returns the welcome message.
     *
     * @return Welcome message.
     */
    public String getWelcomeMessage() {
        return "Hello! I'm Nova.\nWhat can I do for you?";
    }

    /**
     * Returns the goodbye message.
     *
     * @return Goodbye message.
     */
    public String getGoodbyeMessage() {
        return "Bye! See you next time.";
    }

    /**
     * Returns a formatted user-facing error message.
     *
     * @param message Explanation of the error.
     * @return Formatted error message.
     */
    public String getErrorMessage(String message) {
        return "OOPS!!! " + message;
    }

    /**
     * Returns a formatted list of all tasks.
     *
     * @param tasks Tasks to show.
     * @return Formatted task-list message.
     */
    public String getTaskListMessage(TaskList tasks) {
        return formatTaskList("Here are the tasks in your list:", tasks);
    }

    /**
     * Returns a formatted list of matching tasks.
     *
     * @param tasks Matching tasks.
     * @return Formatted matching-task message.
     */
    public String getMatchingTasksMessage(TaskList tasks) {
        return formatTaskList("Here are the matching tasks in your list:", tasks);
    }

    /**
     * Returns a message for a task marked as completed.
     *
     * @param task Marked task.
     * @return Formatted message.
     */
    public String getMarkedMessage(Task task) {
        return "Nice! I've marked this task as done:\n  " + task;
    }

    /**
     * Returns a message for a task marked as incomplete.
     *
     * @param task Unmarked task.
     * @return Formatted message.
     */
    public String getUnmarkedMessage(Task task) {
        return "OK, I've marked this task as not done yet:\n  " + task;
    }

    /**
     * Returns a message for a deleted task.
     *
     * @param task Deleted task.
     * @param taskCount Number of remaining tasks.
     * @return Formatted message.
     */
    public String getDeletedMessage(Task task, int taskCount) {
        return "Noted. I've removed this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list.";
    }

    /**
     * Returns a message for an added task.
     *
     * @param task Added task.
     * @param taskCount Updated task count.
     * @return Formatted message.
     */
    public String getAddedMessage(Task task, int taskCount) {
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list.";
    }

    private String formatTaskList(String heading, TaskList tasks) {
        StringBuilder message = new StringBuilder();
        message.append(HORIZONTAL_LINE).append(System.lineSeparator());
        message.append(heading).append(System.lineSeparator());
        for (int i = 1; i <= tasks.size(); i++) {
            message.append(i).append(". ").append(tasks.getByTaskNumber(i)).append(System.lineSeparator());
        }
        message.append(HORIZONTAL_LINE);
        return message.toString();
    }
}
