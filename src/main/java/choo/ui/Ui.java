package choo.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import choo.task.Task;

/**
 * Handles console input and user-visible output for CHOO.
 */
public class Ui {
    private static final String SEPARATOR =
            "____________________________________________________________";
    private static final String BANNER = String.join(System.lineSeparator(),
            "##### #   # ##### #####",
            "#     #   # #   # #   #",
            "#     ##### #   # #   #",
            "#     #   # #   # #   #",
            "##### #   # ##### #####") + System.lineSeparator();

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a UI connected to the process console.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI using the supplied input and output streams.
     *
     * @param input stream from which commands are read.
     * @param output stream to which responses are written.
     */
    public Ui(InputStream input, PrintStream output) {
        this.scanner = new Scanner(input);
        this.output = output;
    }

    /**
     * Returns whether another command can be read.
     *
     * @return true if another command is available.
     */
    public boolean hasNextCommand() {
        return this.scanner.hasNextLine();
    }

    /**
     * Reads and returns the next command.
     *
     * @return next command line.
     */
    public String readCommand() {
        return this.scanner.nextLine();
    }

    /**
     * Displays the CHOO banner and greeting.
     */
    public void showWelcome() {
        showSeparator();
        this.output.print(BANNER);
        this.output.println("All aboard! I'm CHOO, your task conductor.");
        this.output.println("Tell me what needs to stay on track.");
        showSeparator();
    }

    /**
     * Displays all tasks with one-based task numbers.
     *
     * @param tasks tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        showResponse(formatTaskList(tasks));
    }

    /**
     * Displays tasks whose descriptions match a search keyword.
     *
     * @param tasks Matching tasks to display.
     */
    public void showMatchingTasks(List<Task> tasks) {
        showResponse(formatMatchingTasks(tasks));
    }

    private String formatNumberedTasks(List<Task> tasks) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            result.append(i + 1).append('.').append(tasks.get(i)).append(System.lineSeparator());
        }
        return result.toString();
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task added task.
     * @param taskCount number of tasks after adding.
     */
    public void showAddedTask(Task task, int taskCount) {
        showResponse(formatAddedTask(task, taskCount));
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task deleted task.
     * @param taskCount number of tasks after deleting.
     */
    public void showDeletedTask(Task task, int taskCount) {
        showResponse(formatDeletedTask(task, taskCount));
    }

    /**
     * Displays confirmation that a task completion status changed.
     *
     * @param task updated task.
     * @param isMarked true if the task was marked done.
     */
    public void showTaskStatusChanged(Task task, boolean isMarked) {
        showResponse(formatTaskStatusChanged(task, isMarked));
    }

    /**
     * Displays a user-facing error.
     *
     * @param message error details.
     */
    public void showError(String message) {
        showResponse(formatError(message));
    }

    /**
     * Displays the farewell message.
     */
    public void showBye() {
        showResponse(formatBye());
    }

    /**
     * Displays a complete response followed by the console separator.
     *
     * @param response Response to display.
     */
    public void showResponse(String response) {
        this.output.println(response);
        showSeparator();
    }

    /**
     * Formats all tasks with one-based task numbers.
     *
     * @param tasks Tasks to format.
     * @return Complete task-list response.
     */
    public String formatTaskList(List<Task> tasks) {
        return formatTaskCollection("Here is your task itinerary:", tasks);
    }

    /**
     * Formats tasks that match a search keyword.
     *
     * @param tasks Matching tasks to format.
     * @return Complete search-result response.
     */
    public String formatMatchingTasks(List<Task> tasks) {
        return formatTaskCollection("Here are the matching stops:", tasks);
    }

    /**
     * Formats the task list after its deadlines have been sorted chronologically.
     *
     * @param tasks Sorted tasks to format.
     * @return Complete sorting confirmation and task list.
     */
    public String formatSortedTasks(List<Task> tasks) {
        return formatTaskCollection("Timetable sorted by deadline:", tasks);
    }

    /**
     * Formats confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Number of tasks after adding.
     * @return Complete addition response.
     */
    public String formatAddedTask(Task task, int taskCount) {
        return "Ticket issued. I've added this task:" + System.lineSeparator()
                + "  " + task + System.lineSeparator()
                + formatTaskCount(taskCount);
    }

    /**
     * Formats confirmation that a task was deleted.
     *
     * @param task Deleted task.
     * @param taskCount Number of tasks after deleting.
     * @return Complete deletion response.
     */
    public String formatDeletedTask(Task task, int taskCount) {
        return "Route updated. I've removed this task:" + System.lineSeparator()
                + "  " + task + System.lineSeparator()
                + formatTaskCount(taskCount);
    }

    /**
     * Formats confirmation that a task's completion status changed.
     *
     * @param task Updated task.
     * @param isMarked Whether the task was marked as completed.
     * @return Complete status-change response.
     */
    public String formatTaskStatusChanged(Task task, boolean isMarked) {
        String summary = isMarked
                ? "On track! I've marked this task as done:"
                : "Back on the route. I've marked this task as not done:";
        return summary + System.lineSeparator() + "  " + task;
    }

    /**
     * Formats a user-facing error.
     *
     * @param message Error details.
     * @return Complete error response.
     */
    public String formatError(String message) {
        return "OOPS!!! Signal problem: " + message;
    }

    /**
     * Formats the farewell response.
     *
     * @return Farewell response.
     */
    public String formatBye() {
        return "End of the line for now. Safe travels!";
    }

    private String formatTaskCount(int taskCount) {
        String taskLabel = taskCount == 1 ? "task" : "tasks";
        return "Your itinerary now has " + taskCount + " " + taskLabel + ".";
    }

    private String formatTaskCollection(String heading, List<Task> tasks) {
        String numberedTasks = formatNumberedTasks(tasks).stripTrailing();
        return numberedTasks.isEmpty()
                ? heading
                : heading + System.lineSeparator() + numberedTasks;
    }

    private void showSeparator() {
        this.output.println(SEPARATOR);
    }
}
