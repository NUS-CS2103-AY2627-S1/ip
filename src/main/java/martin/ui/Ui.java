package martin.ui;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import martin.task.Task;
import martin.task.TaskList;

/**
 * Handles all user interface interactions for the Martin chatbot, including
 * reading inputs, formatting outputs, and printing messages.
 */
public class Ui {
    public static final String MARTIN_BANNER = """
            M   M   A   RRR  TTTTT I N   N
            MM MM  A A  R  R   T   I NN  N
            M M M AAAAA RRR    T   I N N N
            M   M A   A R R    T   I N  NN
            M   M A   A R  R   T   I N   N
            """;

    public static final String HORIZ_LINE = "_____________________________________________________";
    public static final String MARTIN_GREETING = "How do you do? I am Martin.\n"
            + "I stand ready to attend to your affairs.";
    public static final String MARTIN_GOODBYE = "I must now take my leave. "
            + "May your affairs remain in excellent order.";

    private static final String GRAY = "\u001B[90m";
    private static final String RESET = "\u001B[0m";

    private final Scanner scanner;

    /**
     * Constructs a new {@code Ui} instance reading from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prints the horizontal divider line.
     */
    public void showLine() {
        System.out.println(HORIZ_LINE);
    }

    /**
     * Displays the initial greeting message and banner.
     */
    public void showWelcome() {
        this.showLine();
        System.out.println(MARTIN_BANNER);
        System.out.println(MARTIN_GREETING);
        this.showLine();
    }

    /**
     * Displays the goodbye message.
     */
    public void showGoodbye() {
        System.out.println(MARTIN_GOODBYE);
        this.showLine();
    }

    /**
     * Reads a line of command input from the user.
     * Returns {@code null} if the input stream has reached EOF or was interrupted.
     *
     * @return The raw command string entered by the user, or {@code null} on EOF.
     */
    public String readCommand() {
        System.out.print("> " + GRAY);
        if (!this.scanner.hasNextLine()) {
            System.out.print(RESET);
            System.out.println();
            return null;
        }

        String input;
        try {
            input = this.scanner.nextLine();
        } catch (NoSuchElementException | IllegalStateException exception) {
            System.out.print(RESET);
            System.out.println();
            return null;
        }

        System.out.print(RESET);
        this.showLine();
        return input;
    }

    /**
     * Displays an error message to the user.
     *
     * @param message The error message to display.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Returns the formatted initial greeting for a graphical user interface.
     *
     * @return The formatted Martin greeting.
     */
    public String formatWelcome() {
        return String.join(System.lineSeparator(), HORIZ_LINE, MARTIN_BANNER.stripTrailing(),
                MARTIN_GREETING, HORIZ_LINE);
    }

    /**
     * Returns the formatted goodbye message.
     *
     * @return The formatted Martin goodbye message.
     */
    public String formatGoodbye() {
        return MARTIN_GOODBYE;
    }

    /**
     * Returns a formatted list of tasks.
     *
     * @param tasks The list of tasks to format.
     * @return The formatted task list.
     */
    public String formatTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "It appears that your list enjoys a state of perfect emptiness.";
        }

        StringBuilder output = new StringBuilder("Your engagements, as requested:");
        this.appendTasks(output, tasks);
        return output.toString();
    }

    /**
     * Returns formatted tasks matching a search keyword.
     *
     * @param tasks The matching tasks to format.
     * @return The formatted search results.
     */
    public String formatFindResults(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "I am obliged to report that no task answers to that description.";
        }

        StringBuilder output = new StringBuilder("These engagements answer to your enquiry:");
        this.appendTasks(output, tasks);
        return output.toString();
    }

    /**
     * Returns a formatted task-added confirmation.
     *
     * @param task       The added task.
     * @param totalTasks The new total number of tasks.
     * @return The formatted confirmation message.
     */
    public String formatTaskAdded(Task task, int totalTasks) {
        return String.format("It is settled. I have entered the following matter:%n%s%n"
                + "Your list now contains %d tasks.",
                task, totalTasks);
    }

    /**
     * Returns a formatted task-deleted confirmation.
     *
     * @param task       The removed task.
     * @param totalTasks The remaining number of tasks.
     * @return The formatted confirmation message.
     */
    public String formatTaskDeleted(Task task, int totalTasks) {
        return String.format("The following matter has been removed from consideration:%n%s%n"
                + "%d tasks remain in your list.",
                task, totalTasks);
    }

    /**
     * Returns a formatted confirmation that a task was marked as completed.
     *
     * @param task The marked task.
     * @return The formatted confirmation message.
     */
    public String formatTaskMarked(Task task) {
        return String.format("The following matter may now be regarded as complete:%n%s", task);
    }

    /**
     * Returns a formatted confirmation that a task was marked as incomplete.
     *
     * @param task The unmarked task.
     * @return The formatted confirmation message.
     */
    public String formatTaskUnmarked(Task task) {
        return String.format("The following matter has been restored to its unfinished state:%n%s", task);
    }

    /**
     * Displays the list of tasks from a {@link TaskList}.
     *
     * @param taskList The task list to display.
     */
    public void showTaskList(TaskList taskList) {
        System.out.println(this.formatTaskList(taskList.getAllTasks()));
    }

    /**
     * Displays the list of tasks.
     *
     * @param tasks The list of tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        System.out.println(this.formatTaskList(tasks));
    }

    /**
     * Displays tasks matching a search keyword.
     *
     * @param tasks The matching tasks to display.
     */
    public void showFindResults(List<Task> tasks) {
        System.out.println(this.formatFindResults(tasks));
    }

    /**
     * Displays confirmation that a task has been added.
     *
     * @param task       The added task.
     * @param totalTasks The new total number of tasks.
     */
    public void showTaskAdded(Task task, int totalTasks) {
        System.out.println(this.formatTaskAdded(task, totalTasks));
    }

    /**
     * Displays confirmation that a task has been deleted.
     *
     * @param task       The removed task.
     * @param totalTasks The remaining number of tasks.
     */
    public void showTaskDeleted(Task task, int totalTasks) {
        System.out.println(this.formatTaskDeleted(task, totalTasks));
    }

    /**
     * Displays confirmation that a task was marked as completed.
     *
     * @param task The marked task.
     */
    public void showTaskMarked(Task task) {
        System.out.println(this.formatTaskMarked(task));
    }

    /**
     * Displays confirmation that a task was marked as not completed.
     *
     * @param task The unmarked task.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println(this.formatTaskUnmarked(task));
    }

    /**
     * Appends numbered task entries to an existing output message.
     *
     * @param output The output being assembled.
     * @param tasks  The tasks to append.
     */
    private void appendTasks(StringBuilder output, List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            output.append(System.lineSeparator())
                    .append(i + 1)
                    .append(". ")
                    .append(tasks.get(i));
        }
    }
}
