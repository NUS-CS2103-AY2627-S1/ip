package nova.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import nova.task.Task;
import nova.task.TaskList.NumberedTask;

/**
 * Handles console input and output for Nova.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";

    private final PrintStream output;
    private final Scanner scanner;

    /**
     * Creates a UI that reads from standard input.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI using the given input and output streams.
     *
     * @param input source of console commands
     * @param output destination for displayed messages
     */
    public Ui(InputStream input, PrintStream output) {
        scanner = new Scanner(input);
        this.output = output;
    }

    /**
     * Displays Nova's greeting.
     */
    public void showWelcome() {
        String banner = " _   _                  \n"
                + "| \\ | | _____   ____ _ \n"
                + "|  \\| |/ _ \\ \\ / / _` |\n"
                + "| |\\  | (_) \\ V / (_| |\n"
                + "|_| \\_|\\___/ \\_/ \\__,_|\n";
        output.println(banner);
        showGuiWelcome();
        showDivider();
    }

    /**
     * Displays Nova's greeting without the console banner or divider.
     */
    public void showGuiWelcome() {
        showLines(
                "Hello! I'm Nova.",
                "Let's take it one task at a time.");
    }

    /**
     * Returns whether another command is available from standard input.
     *
     * @return {@code true} if another command is available
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Returns the next command from standard input.
     *
     * @return next command entered by the user
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the divider between command responses.
     */
    public void showDivider() {
        output.println(DIVIDER);
    }

    /**
     * Displays Nova's farewell.
     */
    public void showGoodbye() {
        output.println(" See you soon. Take care!");
    }

    /**
     * Displays the available Nova commands and their syntax.
     */
    public void showHelp() {
        showLines(
                " Here are Nova's commands:",
                " list",
                " todo DESCRIPTION",
                " deadline DESCRIPTION /by yyyy-MM-dd",
                " event DESCRIPTION /from START /to END",
                " mark TASK_NUMBER",
                " unmark TASK_NUMBER",
                " delete TASK_NUMBER",
                " find KEYWORD",
                " on yyyy-MM-dd",
                " help",
                " bye");
    }

    /**
     * Displays an error message.
     *
     * @param message explanation of the error
     */
    public void showError(String message) {
        output.println(" Oops! " + message);
    }

    /**
     * Displays the number of corrupted records skipped while loading.
     *
     * @param skippedRecordCount number of corrupted records skipped
     */
    public void showSkippedRecords(int skippedRecordCount) {
        output.println(" Oops! I skipped " + skippedRecordCount
                + " corrupted task record(s) in the data file.");
    }

    /**
     * Displays all tasks with their task-list numbers.
     *
     * @param tasks tasks to display
     */
    public void showTasks(List<Task> tasks) {
        output.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays tasks whose descriptions match a find keyword.
     *
     * @param matchingTasks matching tasks to display.
     */
    public void showMatchingTasks(List<NumberedTask> matchingTasks) {
        output.println(" Here are the matching tasks in your list:");
        for (NumberedTask matchingTask : matchingTasks) {
            showNumberedTask(matchingTask.getTaskNumber(), matchingTask.getTask());
        }
    }

    /**
     * Displays a task whose completion state changed.
     *
     * @param task task whose completion state changed
     * @param isMarked whether the task is now completed
     */
    public void showMarkedTask(Task task, boolean isMarked) {
        String message = isMarked
                ? " Nice work! Marked as done:"
                : " No problem. Marked as not done yet:";
        showLines(message, "  " + task);
    }

    /**
     * Displays a deleted task and the remaining task count.
     *
     * @param task deleted task
     * @param taskCount number of tasks remaining
     */
    public void showDeletedTask(Task task, int taskCount) {
        showLines(
                " All right. Removed from your list:",
                "  " + task,
                " Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays an added task and the new task count.
     *
     * @param task added task
     * @param taskCount number of tasks after the addition
     */
    public void showAddedTask(Task task, int taskCount) {
        showLines(
                " Got it! Added to your list:",
                "  " + task,
                " Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays the heading for deadlines that occur on a given date.
     *
     * @param date date whose deadlines will be displayed
     */
    public void showDeadlinesOn(LocalDate date) {
        output.println(" Here are the deadlines on " + date + ":");
    }

    /**
     * Displays a task with its task-list number.
     *
     * @param taskNumber one-based task number
     * @param task task to display
     */
    public void showNumberedTask(int taskNumber, Task task) {
        output.println(" " + taskNumber + "." + task);
    }

    /**
     * Displays each supplied line in order.
     */
    private void showLines(String... lines) {
        for (String line : lines) {
            output.println(line);
        }
    }
}
