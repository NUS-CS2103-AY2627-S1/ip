package trackie.ui;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

import trackie.task.Task;
import trackie.task.TaskList;

/** Reads user commands and displays Trackie's responses. */
public class Ui implements AutoCloseable {
    private static final String BANNER = " _______             _    _\n"
            + "|__   __|           | |  (_)\n"
            + "   | |_ __ __ _  ___| | ___  ___\n"
            + "   | | '__/ _` |/ __| |/ / |/ _ \\\n"
            + "   | | | | (_| | (__|   <| |  __/\n"
            + "   |_|_|  \\__,_|\\___|_|\\_\\_|\\___|\n";
    private final Scanner scanner;
    private final PrintStream output;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        this(new Scanner(System.in), System.out);
    }

    /**
     * Creates a UI using the supplied input and output channels.
     *
     * @param scanner source of user commands
     * @param output destination for responses
     */
    public Ui(Scanner scanner, PrintStream output) {
        this.scanner = scanner;
        this.output = output;
    }

    /** Displays Trackie's greeting. */
    public void showWelcome() {
        this.output.println(BANNER);
        this.output.println("Hi! I'm Trackie, your task-tracking fox.");
        this.output.println("Ready to stay on track?");
    }

    /**
     * Checks whether another command is available on standard input.
     *
     * @return true if another command can be read
     */
    public boolean hasNextCommand() {
        return this.scanner.hasNextLine();
    }

    /**
     * Reads the next command from standard input.
     *
     * @return next command entered by the user
     */
    public String readCommand() {
        return this.scanner.nextLine();
    }

    /**
     * Displays all tasks with one-based numbering.
     *
     * @param tasks tasks to display
     */
    public void showTaskList(TaskList tasks) {
        this.output.println("Here's your trail of tasks:");
        showNumberedTasks(tasks.asList());
    }

    /**
     * Displays matching tasks with one-based result numbering.
     *
     * @param tasks matching tasks to display
     */
    public void showMatchingTasks(List<Task> tasks) {
        this.output.println("Here's what I found on the trail:");
        if (tasks.isEmpty()) {
            this.output.println("No matching tasks found.");
            return;
        }
        showNumberedTasks(tasks);
    }

    /**
     * Displays a successful mark operation.
     *
     * @param task task that was marked
     */
    public void showMarked(Task task) {
        this.output.println("Nice progress! This task is complete:");
        this.output.println("  " + task);
    }

    /**
     * Displays a successful unmark operation.
     *
     * @param task task that was unmarked
     */
    public void showUnmarked(Task task) {
        this.output.println("No worries! This task is back on your trail:");
        this.output.println("  " + task);
    }

    /**
     * Displays a successful task removal and the remaining count.
     *
     * @param task task that was removed
     * @param taskCount number of remaining tasks
     */
    public void showDeleted(Task task, int taskCount) {
        this.output.println("Cleared from the trail! I've removed this task:");
        this.output.println("  " + task);
        showTaskCount(taskCount);
    }

    /**
     * Displays a successful task addition and the updated count.
     *
     * @param task task that was added
     * @param taskCount updated number of tasks
     */
    public void showAdded(Task task, int taskCount) {
        this.output.println("You're on track! I've added this task:");
        this.output.println("  " + task);
        showTaskCount(taskCount);
    }

    /**
     * Displays an error without exposing implementation details.
     *
     * @param message user-facing error explanation
     */
    public void showError(String message) {
        this.output.println(message);
    }

    /** Displays Trackie's farewell. */
    public void showGoodbye() {
        this.output.println("Great tracking today! See you on the next trail!");
    }

    private void showTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        this.output.println("Now you have " + taskCount + " " + taskWord + " in the list.");
    }

    private void showNumberedTasks(List<Task> tasks) {
        IntStream.range(0, tasks.size())
                .mapToObj(index -> (index + 1) + "." + tasks.get(index))
                .forEach(this.output::println);
    }

    /** Releases the input scanner when Trackie exits. */
    @Override
    public void close() {
        this.scanner.close();
    }
}
