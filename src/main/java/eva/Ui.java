package eva;

import java.util.Scanner;

import eva.task.Task;

/**
 * Handles input and output for Eva's text-based interface.
 */
public class Ui {
    private final Scanner scanner;

    /**
     * Creates a text-based user interface.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays Eva's welcome message.
     */
    public void showWelcome() {
        System.out.println(joinLines(
                "Hello, I'm Eva.",
                "Let's take tasks one step at a time."));
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return User command.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays a response in the text-based interface.
     *
     * @param response Response to display.
     */
    public void showResponse(String response) {
        System.out.println(response);
    }

    /**
     * Returns a message containing every task in the list.
     *
     * @param tasks Tasks to include.
     * @return Formatted task-list message.
     */
    public String getTaskListMessage(TaskList tasks) {
        return getTaskListing("Here's your task list:", tasks);
    }

    /**
     * Returns a message containing every matching task.
     *
     * @param tasks Matching tasks to include.
     * @return Formatted matching-task message.
     */
    public String getMatchingTasksMessage(TaskList tasks) {
        return getTaskListing(
                "Here's what I found:", tasks);
    }

    /**
     * Returns a message confirming a newly added task.
     *
     * @param task Added task.
     * @param taskCount Updated number of tasks.
     * @return Task-added confirmation.
     */
    public String getAddedTaskMessage(Task task, int taskCount) {
        return joinLines(
                "All set! I've added this task:",
                "  " + task,
                getTaskCountMessage(taskCount));
    }

    /**
     * Returns a message confirming a deleted task.
     *
     * @param task Deleted task.
     * @param taskCount Updated number of tasks.
     * @return Task-deleted confirmation.
     */
    public String getDeletedTaskMessage(Task task, int taskCount) {
        return joinLines(
                "All set! I've removed this task:",
                "  " + task,
                getTaskCountMessage(taskCount));
    }

    /**
     * Returns a message confirming that a task was marked.
     *
     * @param task Marked task.
     * @return Task-marked confirmation.
     */
    public String getMarkedTaskMessage(Task task) {
        return joinLines(
                "Well done! This task is complete! :)",
                "  " + task);
    }

    /**
     * Returns a message confirming that a task was unmarked.
     *
     * @param task Unmarked task.
     * @return Task-unmarked confirmation.
     */
    public String getUnmarkedTaskMessage(Task task) {
        return joinLines(
                "No problem. This task is pending again:",
                "  " + task);
    }

    /**
     * Returns Eva's farewell message.
     *
     * @return Farewell message.
     */
    public String getByeMessage() {
        return "See you soon. One task at a time!";
    }

    /**
     * Closes the input scanner.
     */
    public void close() {
        scanner.close();
    }

    private String getTaskListing(String heading, TaskList tasks) {
        StringBuilder listing = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            listing.append(System.lineSeparator())
                    .append(i + 1)
                    .append(".")
                    .append(tasks.getTaskAt(i));
        }
        return listing.toString();
    }

    private String getTaskCountMessage(int taskCount) {
        return "You have " + taskCount
                + (taskCount == 1 ? " task" : " tasks")
                + " on your list.";
    }

    /**
     * Joins the supplied lines into one platform-independent message.
     *
     * @param lines Lines to join in order.
     * @return Lines separated by the system line separator.
     */
    private static String joinLines(String... lines) {
        return String.join(System.lineSeparator(), lines);
    }
}
