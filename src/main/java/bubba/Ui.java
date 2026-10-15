package bubba;

import java.util.List;
import java.util.Scanner;

/**
 * Handles console input and all messages shown to the user.
 */
public class Ui {
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Creates a console interface that reads from standard input.
     */
    public Ui() {
    }

    /**
     * Returns the greeting and initial prompt.
     *
     * @return Welcome message.
     */
    public String getWelcomeMessage() {
        return joinLines("Hello! I'm Bubba.", "What can I do for you?");
    }

    /**
     * Reads the next command line, waiting for input when necessary.
     *
     * @return Entered line, or {@code null} if the input stream has ended.
     */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /**
     * Returns the farewell message.
     *
     * @return Farewell message.
     */
    public String getGoodbyeMessage() {
        return "Goodbye. See you again.";
    }

    /**
     * Returns guidance for every command supported by Bubba.
     *
     * @return Command names and their required formats.
     */
    public String getHelpMessage() {
        return joinLines(
                "Here are the commands I understand:",
                "list",
                "todo <description>",
                "deadline <description> /by <d/M/yyyy HHmm>",
                "event <description> /from <d/M/yyyy HHmm> /to <d/M/yyyy HHmm>",
                "mark <task number>",
                "unmark <task number>",
                "delete <task number>",
                "find <keyword>",
                "help",
                "bye");
    }

    /**
     * Returns tasks in order, numbered starting from one.
     *
     * @param tasks Tasks to display.
     * @return Formatted task-list message.
     */
    public String getTaskListMessage(TaskList tasks) {
        StringBuilder message = new StringBuilder("Current list of tasks:");
        int number = 1;
        for (Task task : tasks.toList()) {
            message.append('\n').append(number++).append(". ").append(task);
        }
        return message.toString();
    }

    /**
     * Returns matching tasks in their original order, numbered from one.
     *
     * @param tasks Matching tasks to display.
     * @return Formatted matching-task message.
     */
    public String getMatchingTasksMessage(List<Task> tasks) {
        StringBuilder message = new StringBuilder("Here are the matching tasks in your list:");
        int number = 1;
        for (Task task : tasks) {
            message.append('\n').append(number++).append(". ").append(task);
        }
        return message.toString();
    }

    /**
     * Returns confirmation of an addition and the updated task count.
     *
     * @param task Added task.
     * @param size Total task count after the addition.
     * @return Formatted addition message.
     */
    public String getTaskAddedMessage(Task task, int size) {
        return joinLines("Added new task:", task.toString(), getCountMessage(size));
    }

    /**
     * Returns confirmation of a deletion and the remaining task count.
     *
     * @param task Deleted task.
     * @param size Total task count after the deletion.
     * @return Formatted deletion message.
     */
    public String getTaskDeletedMessage(Task task, int size) {
        return joinLines("Removed the following task:", task.toString(), getCountMessage(size));
    }

    /**
     * Returns confirmation of a task's updated completion status.
     *
     * @param task Updated task.
     * @param isDone Whether the task was marked as completed.
     * @return Formatted status-update message.
     */
    public String getTaskMarkedMessage(Task task, boolean isDone) {
        String message = isDone
                ? "Good job, this task is done!"
                : "This task has been unmarked.";
        return joinLines(message, task.toString());
    }

    private String getCountMessage(int size) {
        return "Number of tasks in list: " + size;
    }

    /**
     * Joins any number of message lines using a newline separator.
     *
     * @param lines Message lines in display order.
     * @return Lines joined into one message.
     */
    private static String joinLines(String... lines) {
        return String.join("\n", lines);
    }

    /**
     * Returns an error with Bubba's error prefix.
     *
     * @param message Explanation of the error.
     * @return Formatted error message.
     */
    public String getErrorMessage(String message) {
        return "My bad... " + message;
    }

    /**
     * Displays a message in the console.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Closes the console scanner and its underlying standard input stream.
     */
    public void close() {
        scanner.close();
    }
}
