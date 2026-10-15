package jaylen;

import java.util.List;
import java.util.Scanner;

/** Formats Jaylen messages and handles interaction with the console user. */
public class Ui {
    private static final String SEPARATOR = "_".repeat(60);
    private static final String ERROR_PREFIX = " OOPS!!! ";
    private static final String BANNER = """
            ╔══════════════════════╗
            ║        JAYLEN        ║
            ╚══════════════════════╝
            """.stripTrailing();

    private final Scanner scanner;

    /** Creates a console UI that reads from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays Jaylen's welcome message. */
    public void showWelcome() {
        showLine();
        System.out.println(BANNER);
        System.out.println(getWelcomeMessage());
        showLine();
    }

    /**
     * Returns the welcome text shared by the console and graphical interfaces.
     *
     * @return the welcome text
     */
    public String getWelcomeMessage() {
        return "Hello! I'm Jaylen.\nWhat can I do for you?";
    }

    /**
     * Reads one command from the console.
     *
     * @return the next line entered by the user
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays the standard divider line. */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /**
     * Displays a user-facing error.
     *
     * @param message the explanation to display
     */
    public void showError(String message) {
        System.out.println(getErrorMessage(message));
    }

    /**
     * Formats an error for display in either interface.
     *
     * @param message the explanation to display
     * @return the formatted error
     */
    public String getErrorMessage(String message) {
        return ERROR_PREFIX + message;
    }

    /**
     * Returns whether text is a formatted Jaylen error response.
     *
     * @param message the response to inspect
     * @return {@code true} when the response begins with the error prefix
     */
    public boolean isErrorMessage(String message) {
        return message != null && message.startsWith(ERROR_PREFIX);
    }

    /**
     * Displays the tasks in their current order.
     *
     * @param tasks the tasks to display
     */
    public void showTasks(TaskList tasks) {
        System.out.println(getTasksMessage(tasks));
    }

    /**
     * Formats all tasks in their current order.
     *
     * @param tasks the tasks to display
     * @return the formatted task list
     */
    public String getTasksMessage(TaskList tasks) {
        StringBuilder message = new StringBuilder(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n ").append(i + 1).append(".").append(tasks.get(i + 1));
        }
        return message.toString();
    }

    /**
     * Displays tasks whose descriptions contain all supplied search terms.
     *
     * @param tasks the tasks to search
     * @param query the space-separated search terms
     */
    public void showMatchingTasks(TaskList tasks, String query) {
        System.out.println(getMatchingTasksMessage(tasks, query));
    }

    /**
     * Formats the tasks whose descriptions contain all supplied search terms.
     *
     * @param tasks the tasks to search
     * @param query the space-separated search terms
     * @return the formatted matching tasks
     */
    public String getMatchingTasksMessage(TaskList tasks, String query) {
        StringBuilder message = new StringBuilder(" Here are the matching tasks in your list:");
        List<Integer> matchingPositions = tasks.find(query);
        if (matchingPositions.isEmpty()) {
            return message.append("\n No matching tasks found.").toString();
        }
        for (int position : matchingPositions) {
            message.append("\n ").append(position).append(".").append(tasks.get(position));
        }
        return message.toString();
    }

    /**
     * Displays the confirmation for a newly added task.
     *
     * @param task the task that was added
     * @param taskCount the number of tasks after adding it
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(getTaskAddedMessage(task, taskCount));
    }

    /**
     * Formats the confirmation for a newly added task.
     *
     * @param task the task that was added
     * @param taskCount the number of tasks after adding it
     * @return the formatted confirmation
     */
    public String getTaskAddedMessage(Task task, int taskCount) {
        return " Got it. I've added this task:\n"
                + "   " + task + "\n"
                + " Now you have " + formatTaskCount(taskCount) + " in the list.";
    }

    /**
     * Displays the confirmation for marking a task done.
     *
     * @param task the task that was marked done
     */
    public void showMarked(Task task) {
        System.out.println(getMarkedMessage(task));
    }

    /**
     * Formats the confirmation for marking a task done.
     *
     * @param task the task that was marked done
     * @return the formatted confirmation
     */
    public String getMarkedMessage(Task task) {
        return " Nice! I've marked this task as done:\n   " + task;
    }

    /**
     * Displays the confirmation for marking a task undone.
     *
     * @param task the task that was marked undone
     */
    public void showUnmarked(Task task) {
        System.out.println(getUnmarkedMessage(task));
    }

    /**
     * Formats the confirmation for marking a task undone.
     *
     * @param task the task that was marked undone
     * @return the formatted confirmation
     */
    public String getUnmarkedMessage(Task task) {
        return " OK, I've marked this task as not done yet:\n   " + task;
    }

    /**
     * Displays the confirmation for deleting a task.
     *
     * @param task the task that was deleted
     * @param remainingTasks the number of tasks left after deletion
     */
    public void showDeleted(Task task, int remainingTasks) {
        System.out.println(getDeletedMessage(task, remainingTasks));
    }

    /**
     * Formats the confirmation for deleting a task.
     *
     * @param task the task that was deleted
     * @param remainingTasks the number of tasks left after deletion
     * @return the formatted confirmation
     */
    public String getDeletedMessage(Task task, int remainingTasks) {
        return " Noted. I've removed this task:\n"
                + "   " + task + "\n"
                + " Now you have " + formatTaskCount(remainingTasks) + " in the list.";
    }

    /** Displays the farewell message. */
    public void showGoodbye() {
        System.out.println(getGoodbyeMessage());
    }

    /**
     * Returns the farewell shared by the console and graphical interfaces.
     *
     * @return the farewell message
     */
    public String getGoodbyeMessage() {
        return "Bye. Hope to see you again soon!";
    }

    private String formatTaskCount(int taskCount) {
        return taskCount + (taskCount == 1 ? " task" : " tasks");
    }
}
