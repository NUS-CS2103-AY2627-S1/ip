package yqr.ui;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Consumer;

import yqr.task.Task;

/**
 * Handles input from and output to the user.
 */
public class Ui {
    private static final String SEPARATOR =
            "____________________________________________________________";
    private static final String BANNER = "+-------+\n"
            + "|  yqr  |\n"
            + "+-------+";

    private final Scanner scanner;
    private final Consumer<String> output;

    /**
     * Creates a user interface connected to standard input.
     */
    public Ui() {
        this(new Scanner(System.in), System.out::println);
    }

    /**
     * Creates a user interface connected to the given input and output.
     *
     * @param scanner scanner used to read commands.
     * @param output consumer that receives displayed lines.
     */
    public Ui(Scanner scanner, Consumer<String> output) {
        this.scanner = Objects.requireNonNull(scanner);
        this.output = Objects.requireNonNull(output);
    }

    /**
     * Creates an output-only user interface that sends each displayed line to a consumer.
     *
     * @param output consumer that receives displayed lines.
     */
    public Ui(Consumer<String> output) {
        scanner = null;
        this.output = Objects.requireNonNull(output);
    }

    /** Displays the chatbot greeting. */
    public void showWelcome() {
        showLine();
        showLines(BANNER, "Hello! I'm yqr.", "What can I do for you?");
        showLine();
    }

    /**
     * Returns whether another command can be read from standard input.
     *
     * @return {@code true} if another command is available.
     */
    public boolean hasNextCommand() {
        if (scanner == null) {
            return false;
        }
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command entered by the user.
     *
     * @return next user command.
     */
    public String readCommand() {
        if (scanner == null) {
            throw new IllegalStateException("This user interface has no input source");
        }
        return scanner.nextLine().trim();
    }

    /** Displays the chatbot farewell. */
    public void showGoodbye() {
        showLines("Bye. Hope to see you again soon!");
    }

    /**
     * Displays an error encountered while loading saved tasks.
     *
     * @param message explanation of the loading error.
     */
    public void showLoadingError(String message) {
        showLines(message, "Starting with an empty task list instead.");
        showLine();
    }

    /**
     * Displays an error caused by a command or storage operation.
     *
     * @param message explanation of the error.
     */
    public void showError(String message) {
        showLines(message);
    }

    /** Displays confirmation that the most recent task change was undone. */
    public void showUndoSuccess() {
        showLines("Done. The most recent change has been undone.");
    }

    /**
     * Displays all tasks in numbered order.
     *
     * @param tasks tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            showLines("There are no tasks in your list.");
            return;
        }
        showLines("Here are the tasks in your list:");
        showNumberedTasks(tasks);
    }

    /**
     * Displays tasks whose descriptions match a search keyword.
     *
     * @param tasks matching tasks to display.
     */
    public void showMatchingTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            showLines("There are no matching tasks in your list.");
            return;
        }
        showLines("Here are the matching tasks in your list:");
        showNumberedTasks(tasks);
    }

    /**
     * Displays tasks in their list order using one-based numbering.
     *
     * @param tasks tasks to display.
     */
    private void showNumberedTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            showLines((i + 1) + ". " + tasks.get(i));
        }
    }

    /**
     * Displays confirmation that a task was marked or unmarked.
     *
     * @param task task whose status changed.
     * @param isMarked whether the task was marked as done.
     */
    public void showTaskStatusChange(Task task, boolean isMarked) {
        String message;
        if (isMarked) {
            message = "Nice! I've marked this task as done:";
        } else {
            message = "OK, I've marked this task as not done yet:";
        }
        showLines(message, "  " + task);
    }

    /**
     * Displays confirmation that a task was deleted and the new task count.
     *
     * @param task deleted task.
     * @param taskCount number of tasks remaining.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showLines("Noted. I've removed this task:", "  " + task);
        showTaskCount(taskCount);
    }

    /**
     * Displays confirmation that a task was added and the new task count.
     *
     * @param task added task.
     * @param taskCount number of tasks now stored.
     */
    public void showTaskAdded(Task task, int taskCount) {
        showLines("Got it. I've added this task:", "  " + task);
        showTaskCount(taskCount);
    }

    /** Displays the line separating command responses. */
    public void showLine() {
        showLines(SEPARATOR);
    }

    /**
     * Displays a task count with the correct singular or plural noun.
     *
     * @param taskCount number of tasks to display.
     */
    private void showTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        showLines("Now you have " + taskCount + " " + taskWord + " in the list.");
    }

    /**
     * Sends any number of lines to the configured output in their given order.
     *
     * @param lines lines to display.
     */
    private void showLines(String... lines) {
        for (String line : lines) {
            output.accept(line);
        }
    }
}
