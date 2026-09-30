package kong.ui;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Consumer;

import kong.task.Task;
import kong.task.TaskList;

/**
 * Handles interactions with the user through the console.
 */
public class Ui {
    private static final String LINE = "________________________________________";
    private static final String BANNER = " _  __                 \n"
            + "| |/ /___  _ __   __ _ \n"
            + "| ' // _ \\| '_ \\ / _` |\n"
            + "| . \\ (_) | | | | (_| |\n"
            + "|_|\\_\\___/|_| |_|\\__, |\n"
            + "                 |___/ \n";
    private final Scanner scanner;
    private final Consumer<String> output;

    /** Creates a console UI that reads from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
        output = System.out::println;
    }

    /**
     * Creates a response UI that sends each displayed message to the supplied consumer.
     *
     * @param output destination for user-facing messages
     */
    public Ui(Consumer<String> output) {
        scanner = null;
        this.output = output;
    }

    /** Displays Professor Kong's banner and greeting. */
    public void showWelcome() {
        output.accept(BANNER + LINE + "\n"
                + "Greetings, esteemed colleague! I am Professor Kong.\n"
                + "What grand endeavor or academic inquiry shall we pursue today?");
    }

    /** Displays a divider between command interactions. */
    public void showLine() {
        output.accept(LINE);
    }

    /**
     * Checks whether another line of command input is available from standard input.
     *
     * @return {@code true} if there is another line of input; {@code false} otherwise
     */
    public boolean hasNextCommand() {
        return scanner != null && scanner.hasNextLine();
    }

    /**
     * Reads the user's next command line.
     *
     * @return entered command text
     */
    public String readCommand() {
        assert scanner != null : "A response-only UI cannot read commands";
        return scanner.nextLine();
    }

    /**
     * Displays a user-facing error.
     *
     * @param message error explanation
     */
    public void showError(String message) {
        output.accept(message);
    }

    /** Displays a warning when stored tasks cannot be loaded. */
    public void showLoadingError() {
        output.accept("Heavens! Unable to load tasks from disk. Starting with an empty archive.");
    }

    /**
     * Displays all current tasks or an empty-list message.
     *
     * @param tasks task list to display
     */
    public void showTaskList(TaskList tasks) {
        if (tasks.isEmpty()) {
            output.accept("Your archive is pristine. There are currently no tasks in your list.");
            return;
        }

        output.accept("Here are the tasks currently recorded in your archives:");
        showNumberedTasks(tasks.asList());
    }

    /**
     * Displays tasks occurring on a requested date.
     *
     * @param matchingTasks tasks matching the date query
     */
    public void showTasksOnDate(ArrayList<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            output.accept("The ledger is clear. There are no deadlines or events on this date.");
            return;
        }

        output.accept("Consulting the ledger. Here are the deadlines and events on this date:");
        showNumberedTasks(matchingTasks);
    }

    /**
     * Displays tasks matching a search keyword, or a no-match message.
     *
     * @param matchingTasks tasks matching the search keyword
     */
    public void showMatchingTasks(ArrayList<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            output.accept("Search complete. There are no matching tasks in your list.");
            return;
        }

        output.accept("Eureka! Here are the matching tasks found in your archives:");
        showNumberedTasks(matchingTasks);
    }

    /** Displays tasks in encounter order using one-based numbering. */
    private void showNumberedTasks(Iterable<Task> tasks) {
        int taskNumber = 1;
        for (Task task : tasks) {
            output.accept(String.format("%d. %s", taskNumber, task));
            taskNumber++;
        }
    }

    /**
     * Confirms that a task was added.
     *
     * @param task added task
     */
    public void showTaskAdded(Task task) {
        output.accept("Splendid addition! I have inscribed this task into your archives:");
        output.accept(task.toString());
    }

    /**
     * Confirms that a task was marked complete.
     *
     * @param task updated task
     */
    public void showTaskMarked(Task task) {
        output.accept("Capital progress! I have marked this task as completed:");
        output.accept(task.toString());
    }

    /**
     * Confirms that a task was marked incomplete.
     *
     * @param task updated task
     */
    public void showTaskUnmarked(Task task) {
        output.accept("Back to the drawing board! I have restored this task to pending status:");
        output.accept(task.toString());
    }

    /**
     * Confirms that a task was removed.
     *
     * @param task removed task
     */
    public void showTaskDeleted(Task task) {
        output.accept("Expunged from the archives! The following task has been removed:");
        output.accept(task.toString());
    }

    /** Displays the exit message. */
    public void showGoodbye() {
        output.accept("Cheerio! Until our next scholarly consultation.");
    }
}
