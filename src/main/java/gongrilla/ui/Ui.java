package gongrilla.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

import gongrilla.task.Task;

/**
 * Handles all console input and output for gongrilla.Gongrilla.
 */
public class Ui {
    private static final String HORIZONTAL_LINE =
            "____________________________________________________________";
    private static final String BANNER =
              "  _--==--_  \n"
            + " / _    _ \\ \n"
            + " \\        / \n"
            + " |  (..)  |  \n"
            + " \\   __   / \n"
            + "  \\______/  \n";

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a UI connected to the standard console streams.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI using the supplied streams, which also makes the UI testable.
     *
     * @param input source of user commands.
     * @param output destination for chatbot responses.
     */
    public Ui(InputStream input, PrintStream output) {
        this.scanner = new Scanner(input);
        this.output = output;
    }

    /** Shows the startup banner and greeting. */
    public void showWelcome() {
        showLine();
        output.print(BANNER);
        printLines(
                "higa higa click click",
                "Human back. Gongrilla ready. Type help for commands.");
        showLine();
    }

    /**
     * Checks whether the input stream contains another command.
     *
     * @return whether another command is available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command and removes surrounding whitespace.
     *
     * @return the next normalized command.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Shows the standard divider between interactions. */
    public void showLine() {
        output.println(HORIZONTAL_LINE);
    }

    /** Prints each supplied line in order using the configured output stream. */
    private void printLines(String... lines) {
        for (String line : lines) {
            output.println(line);
        }
    }

    /** Shows the command reference with syntax and examples. */
    public void showHelp() {
        printLines(
                "Neigh Neigh Neigh:",
                "",
                "Start here",
                "help - show this guide",
                "list - show all tasks",
                "find <keyword> - search task names",
                "bye - say goodbye (exits the console)",
                "",
                "Add tasks",
                "todo <task> - add a task",
                "deadline <task> /by <date> - add a deadline",
                "event <task> /from <date> /to <date> - add an event",
                "Replace <...> with your own words.",
                "",
                "Update tasks",
                "mark <number> - mark done",
                "unmark <number> - mark incomplete",
                "delete <number> - remove a task",
                "Use task numbers from list, not find. Numbers start at 1.",
                "",
                "Dates:",
                "D/M/YYYY or YYYY-MM-DD; optional 24-hour HHMM.",
                "if no time, Gongrilla use midnight. Event end must be after start.",
                " ",
                "Examples:",
                "todo buy bananas",
                "deadline buy bananas /by 20/9/2026 1800",
                "event lunch /from 20/9/2026 1200 /to 20/9/2026 1300",
                "mark 1");
    }

    /** Shows the farewell message. */
    public void showGoodbye() {
        output.println("Fine. Bring banana next time.");
    }

    /**
     * Shows every task with its user-facing one-based number.
     *
     * @param tasks tasks to display in their current order.
     */
    public void showTaskList(List<Task> tasks) {
        output.println("Gongrilla find tasks in list:");
        printNumberedTasks(tasks);
    }

    /**
     * Shows tasks that match a find command with result-local numbering.
     *
     * @param tasks matching tasks in their original list order.
     */
    public void showMatchingTasks(List<Task> tasks) {
        output.println("Here are the matching tasks in your list:");
        printNumberedTasks(tasks);
    }

    /** Prints tasks in list order with numbering starting at one for this result. */
    private void printNumberedTasks(List<Task> tasks) {
        IntStream.range(0, tasks.size())
                .mapToObj(index -> "  " + (index + 1) + "." + tasks.get(index))
                .forEachOrdered(output::println);
    }

    /**
     * Shows a newly added task and the updated task count.
     *
     * @param taskType user-facing name of the task type.
     * @param task task that was added.
     * @param taskCount number of tasks after the addition.
     */
    public void showAddedTask(String taskType, Task task, int taskCount) {
        printLines(
                "higa higa click click. New " + taskType + ":",
                "  " + task,
                "Gongrilla count " + taskCount + " tasks.");
    }

    /**
     * Shows a deleted task and the updated task count.
     *
     * @param task task that was deleted.
     * @param taskCount number of tasks after the deletion.
     */
    public void showDeletedTask(Task task, int taskCount) {
        printLines(
                "Gongrilla remove task:",
                "  " + task,
                "Now Gongrilla count " + taskCount + " tasks in list.");
    }

    /**
     * Shows that a task was marked complete.
     *
     * @param task task whose state changed.
     */
    public void showMarkedTask(Task task) {
        printLines(
                "Marked. oink oink \uD83D\uDC34",
                "  " + task.getIsDoneStatus() + " " + task.getName());
    }

    /**
     * Shows that a task was marked incomplete.
     *
     * @param task task whose state changed.
     */
    public void showUnmarkedTask(Task task) {
        printLines(
                "Unmarked. \uD83D\uDC34\uD83D\uDC94",
                "  " + task.getIsDoneStatus() + " " + task.getName());
    }

    /**
     * Shows a user-facing error message.
     *
     * @param message explanation of the error.
     */
    public void showError(String message) {
        printLines(message, "Type help. Gongrilla show commands.");
    }

    /**
     * Shows an error that prevents saved tasks from loading.
     *
     * @param message explanation supplied by the storage layer.
     */
    public void showLoadingError(String message) {
        printLines(
                "Gongrilla cannot read saved tasks: " + message,
                "Fix data file, then start gongrilla.Gongrilla again.");
        showLine();
    }

    /**
     * Shows a persistence error for a command whose change was not applied.
     *
     * @param message explanation supplied by the storage layer.
     */
    public void showSavingError(String message) {
        printLines(
                "Gongrilla cannot save that change: " + message,
                "Task list was not changed.",
                "Check file permissions, free disk space, and other Gongrilla windows before retrying.");
    }
}
