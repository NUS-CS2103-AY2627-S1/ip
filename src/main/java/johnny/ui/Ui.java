package johnny.ui;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Consumer;

import johnny.task.Task;
import johnny.task.TaskList;

/**
 * Handles all interactions with the user, including reading input
 * and displaying formatted output.
 */
public class Ui {

    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = "     _       _                       \n"
            + "    | | ___ | |__  _ __  _ __  _   _ \n"
            + " _  | |/ _ \\| '_ \\| '_ \\| '_ \\| | | |\n"
            + "| |_| | (_) | | | | | | | | | | |_| |\n"
            + " \\___/ \\___/|_| |_|_| |_|_| |_|\\__, |\n"
            + "                                |___/ ";

    private final Scanner scanner;
    private final Consumer<String> output;

    /**
     * Creates a UI that reads from standard input and writes to standard output.
     */
    public Ui() {
        scanner = new Scanner(System.in);
        output = System.out::println;
    }

    /**
     * Creates a UI that sends output to the supplied destination.
     *
     * @param output destination for each complete line of output.
     */
    public Ui(Consumer<String> output) {
        scanner = null;
        this.output = output;
    }

    /**
     * Returns whether another line of console input is available.
     *
     * @return true if another line can be read.
     */
    public boolean hasNextLine() {
        return scanner != null && scanner.hasNextLine();
    }

    /**
     * Reads the next line of console input.
     *
     * @return next input line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Closes the console input scanner, if present.
     */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }

    /**
     * Displays a horizontal divider line.
     */
    public void showLine() {
        output.accept("    " + LINE);
    }

    private void printIndented(String... messages) {
        for (String message : messages) {
            output.accept("     " + message);
        }
    }

    /**
     * Displays the welcome banner and greeting message.
     */
    public void showGreeting() {
        showLine();
        output.accept(BANNER);
        printIndented("Good day. Johnny at your service.",
                "How may I keep your day in order?");
        showLine();
    }

    /**
     * Displays the farewell message.
     */
    public void showFarewell() {
        printIndented("Until next time. I'll keep things in order.");
    }

    /**
     * Displays the warning used when saved tasks cannot be loaded.
     */
    public void showLoadingError() {
        printIndented("A note, if I may: I couldn't load your agenda, so we'll start afresh.");
    }

    /**
     * Displays the warning used when tasks cannot be saved.
     */
    public void showSaveError() {
        printIndented("A note, if I may: I couldn't save your agenda.");
    }

    /**
     * Displays a command error.
     *
     * @param message explanation of the error.
     */
    public void showError(String message) {
        printIndented("I'm afraid something is amiss: " + message);
    }

    /**
     * Displays a confirmation message after a task is added.
     *
     * @param task task that was added.
     * @param taskCount number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        assert taskCount > 0 : "Task count must be positive after addition";
        printIndented("Consider it noted:",
                "  " + task,
                "Your agenda now has " + taskCount + (taskCount == 1 ? " task." : " tasks."));
    }

    /**
     * Displays a confirmation message after a task is marked as done.
     *
     * @param task task that was marked.
     */
    public void showTaskMarked(Task task) {
        printIndented("Excellent. One task completed:",
                "  " + task);
    }

    /**
     * Displays a confirmation message after a task is unmarked.
     *
     * @param task task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        printIndented("Very well. This task is pending again:",
                "  " + task);
    }

    /**
     * Displays a confirmation message after a task is deleted.
     *
     * @param task task that was deleted.
     * @param taskCount number of tasks after deletion.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        assert taskCount >= 0 : "Task count must not be negative after deletion";
        printIndented("Removed from the agenda:",
                "  " + task,
                "Your agenda now has " + taskCount + (taskCount == 1 ? " task." : " tasks."));
    }

    /** Displays a confirmation after the most recent task change is undone. */
    public void showCommandUndone() {
        printIndented("As you wish. The last change has been undone.");
    }

    /**
     * Displays all tasks in the list, numbered starting from 1.
     *
     * @param tasks tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        printIndented("Here is your current agenda:");
        for (int i = 0; i < tasks.size(); i++) {
            printIndented((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays matching tasks, numbered starting from 1.
     *
     * @param matches matching tasks to display.
     */
    public void showFindResults(ArrayList<Task> matches) {
        printIndented("These entries match your request:");
        for (int i = 0; i < matches.size(); i++) {
            printIndented((i + 1) + "." + matches.get(i));
        }
    }
}
