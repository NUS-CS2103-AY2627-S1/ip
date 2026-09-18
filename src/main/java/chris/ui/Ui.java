package chris.ui;

import java.util.List;
import java.util.Scanner;

import chris.task.Task;
import chris.task.TaskList;

/**
 * Handles console input and output for the chatbot.
 */
public class Ui {
    private static final String BANNER = "  ____ _          _     \n"
            + " / ___| |__  _ __(_)___ \n"
            + "| |   | '_ \\| '__| / __|\n"
            + "| |___| | | | |  | \\__ \\\n"
            + " \\____|_| |_|_|  |_|___/";
    private static final String LINE = "____________________________________________________________";

    private final Scanner scanner;

    /**
     * Creates a UI that reads from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the chatbot greeting.
     */
    public void showWelcome() {
        showLine();
        showMessage(getWelcomeMessage());
        showLine();
    }

    /**
     * Returns the chatbot banner and greeting.
     *
     * @return Welcome message.
     */
    public String getWelcomeMessage() {
        return BANNER + System.lineSeparator()
                + "Hello! I'm Chris." + System.lineSeparator()
                + "What can I do for you?";
    }

    /**
     * Returns whether another command can be read.
     *
     * @return {@code true} if another command is available.
     */
    public boolean hasNextCommand() {
        return this.scanner.hasNextLine();
    }

    /**
     * Reads and trims the next user command.
     *
     * @return Next command entered by the user.
     */
    public String readCommand() {
        return this.scanner.nextLine().trim();
    }

    /**
     * Displays the horizontal divider line.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Returns the chatbot farewell message.
     *
     * @return Farewell message.
     */
    public String getGoodbyeMessage() {
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Formats all tasks with one-based numbering.
     *
     * @param tasks Tasks to format.
     * @return Formatted task list.
     */
    public String formatTaskList(TaskList tasks) {
        return formatTasks("Here are the tasks in your list:", tasks.asList());
    }

    /**
     * Formats tasks that match a search keyword.
     *
     * @param matchingTasks Matching tasks to format.
     * @return Formatted matching tasks.
     */
    public String formatMatchingTasks(List<Task> matchingTasks) {
        return formatTasks("Here are the matching tasks in your list:", matchingTasks);
    }

    /**
     * Formats confirmation that a task was completed.
     *
     * @param task Updated task.
     * @return Task completion confirmation.
     */
    public String formatTaskMarked(Task task) {
        return "Nice! I've marked this task as done:" + System.lineSeparator() + "  " + task;
    }

    /**
     * Formats confirmation that a task was marked incomplete.
     *
     * @param task Updated task.
     * @return Task status confirmation.
     */
    public String formatTaskUnmarked(Task task) {
        return "OK, I've marked this task as not done yet:" + System.lineSeparator() + "  " + task;
    }

    /**
     * Formats confirmation that a task was removed.
     *
     * @param task Removed task.
     * @param taskCount Number of remaining tasks.
     * @return Task deletion confirmation.
     */
    public String formatTaskDeleted(Task task, int taskCount) {
        return "Noted. I've removed this task:" + System.lineSeparator()
                + "  " + task + System.lineSeparator()
                + formatTaskCount(taskCount);
    }

    /**
     * Formats confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Number of stored tasks.
     * @return Task addition confirmation.
     */
    public String formatTaskAdded(Task task, int taskCount) {
        return "Got it. I've added this task:" + System.lineSeparator()
                + "  " + task + System.lineSeparator()
                + formatTaskCount(taskCount);
    }

    /**
     * Displays a user-facing error message.
     *
     * @param message Explanation of the error.
     */
    public void showError(String message) {
        showMessage(formatError(message));
    }

    /**
     * Formats a user-facing error message.
     *
     * @param message Explanation of the error.
     * @return Formatted error message.
     */
    public String formatError(String message) {
        return "OOPS!!! " + message;
    }

    /**
     * Displays a complete chatbot response.
     *
     * @param message Response to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Closes the input scanner.
     */
    public void close() {
        this.scanner.close();
    }

    private String formatTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        return "Now you have " + taskCount + " " + taskWord + " in the list.";
    }

    private String formatTasks(String heading, List<Task> tasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int index = 0; index < tasks.size(); index++) {
            response.append(System.lineSeparator())
                    .append(index + 1)
                    .append('.')
                    .append(tasks.get(index));
        }
        return response.toString();
    }
}
