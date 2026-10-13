package duke;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * Handles all console input and output for Wangsa.
 */
public class Ui implements AutoCloseable {
    private static final String SEPARATOR = "____________________________________________________________";

    private static final String BANNER = "Wangsa";

    private final Scanner scanner;

    private final PrintStream output;

    /**
     * Creates a UI connected to the process's standard input and output.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI connected to the supplied streams.
     *
     * @param input Source of user commands.
     * @param output Destination for chatbot messages.
     */
    public Ui(InputStream input, PrintStream output) {
        this.scanner = new Scanner(input);
        this.output = output;
    }

    /**
     * Displays the greeting shown when Wangsa starts.
     */
    public void showWelcome() {
        showLine();
        output.println(BANNER);
        output.println("Hello! I'm Wangsa.");
        output.println("What can I do for you?");
        showLine();
    }

    /**
     * Returns whether another command can be read.
     *
     * @return Whether another command is available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and returns the next complete user command.
     *
     * @return The command text.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the separator used around each chatbot response.
     */
    public void showLine() {
        output.println(SEPARATOR);
    }

    /**
     * Displays Wangsa's farewell.
     */
    public void showGoodbye() {
        output.println("Bye. Hope to see you again soon!");
    }

    /**
     * Displays command help or an AI answer using console-safe punctuation.
     *
     * @param message Response to display.
     */
    public void showMessage(String message) {
        output.println(message.replace("•", "-").replace("—", "--"));
    }

    /**
     * Displays all tasks in their current order.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        showTasks("Here are the tasks in your list:", tasks);
    }

    /**
     * Displays tasks matching a search keyword.
     *
     * @param matches Matching tasks with their full-list task numbers.
     */
    public void showMatchingTasks(List<TaskMatch> matches) {
        if (matches.isEmpty()) {
            output.println("No matching tasks found.");
            return;
        }
        output.println("Here are the matching tasks in your list:");
        for (TaskMatch match : matches) {
            output.println(match.taskNumber() + "." + match.task());
        }
    }

    /**
     * Displays tasks ordered by deadline, with undated tasks after dated tasks.
     *
     * @param tasks Sorted tasks to display.
     */
    public void showSortedTaskList(List<Task> tasks) {
        showTasks("Here are your tasks sorted by deadline (undated tasks last):", tasks);
    }

    /**
     * Displays a heading followed by tasks in their current order.
     */
    private void showTasks(String heading, List<Task> tasks) {
        if (tasks.isEmpty()) {
            output.println("Your list is empty. Try `todo read a book` to add your first task.");
            return;
        }
        output.println(heading);
        for (int i = 0; i < tasks.size(); i++) {
            output.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays confirmation that a task has been added and saved.
     *
     * @param task Added task.
     * @param taskCount Resulting task count.
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println("Got it. I've added this task:");
        output.println("  " + task);
        showTaskCount(taskCount);
    }

    /**
     * Displays confirmation that a task's completion status has been saved.
     *
     * @param task Updated task.
     * @param isMarked Whether the task is now complete.
     */
    public void showTaskStatusUpdate(Task task, boolean isMarked) {
        if (isMarked) {
            output.println("Nice! I've marked this task as done:");
        } else {
            output.println("I've reopened this task for you:");
        }
        output.println("  " + task);
    }

    /**
     * Displays confirmation that a task has been deleted and the change saved.
     *
     * @param task Removed task.
     * @param taskCount Resulting task count.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println("Noted. I've removed this task:");
        output.println("  " + task);
        showTaskCount(taskCount);
    }

    /**
     * Uses the singular form when the list contains exactly one task.
     */
    private void showTaskCount(int taskCount) {
        output.println("You now have " + taskCount + (taskCount == 1 ? " task" : " tasks") + " in your list.");
    }

    /**
     * Displays an error that Wangsa can explain to the user.
     *
     * @param message User-facing error.
     */
    public void showError(String message) {
        output.println(message);
    }

    /**
     * Releases the scanner used to read commands.
     */
    @Override
    public void close() {
        scanner.close();
    }
}
