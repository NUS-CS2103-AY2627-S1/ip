package turtley.ui;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import turtley.exception.TurtleyException;
import turtley.model.Task;

/**
 * Handles Turtley's console input and user-facing output.
 */
public class Ui {

    private static final String SEPARATOR = "____________________________________________________________";
    private static final String BANNER = "      _____________ \n"
            + "__   /__|_______|__\\ \n"
            + "\\^ \\/______|_|______\\\n"
            + " \\ /_______|_|_______\\>\n"
            + "   |_/ |_/     \\_| \\_|\n Turtley";

    private final PrintStream output;
    private final boolean shouldShowSeparators;
    private boolean hasError;

    /**
     * Creates a UI that writes to standard output.
     */
    public Ui() {
        this(System.out, true);
    }

    /**
     * Creates a UI that writes to the supplied output stream.
     *
     * @param output the stream used for user-facing output.
     */
    public Ui(PrintStream output) {
        this(output, true);
    }

    /**
     * Creates a UI that writes to the supplied output stream, optionally including separators.
     *
     * @param output the stream used for user-facing output.
     * @param shouldShowSeparators whether standard separator lines should be printed.
     */
    public Ui(PrintStream output, boolean shouldShowSeparators) {
        this.output = Objects.requireNonNull(output);
        this.shouldShowSeparators = shouldShowSeparators;
    }

    /**
     * Reads one line from the user.
     *
     * @param keyboard the scanner connected to user input.
     * @return the next input line, or {@code null} when input has ended.
     */
    public String readLine(Scanner keyboard) {
        if (!keyboard.hasNextLine()) {
            return null;
        }
        return keyboard.nextLine();
    }

    /**
     * Displays Turtley's startup greeting.
     */
    public void showWelcome() {
        showSeparator();
        output.println(BANNER);
        output.println("Hello! I'm Turtley.");
        output.println("What can I do for you? o/T\\>");
        showSeparator();
    }

    /**
     * Displays the normal shutdown message.
     */
    public void showGoodbye() {
        showSeparator();
        output.println("Bye. See you around! o/T\\>");
        showSeparator();
    }

    /**
     * Displays an application error with Turtley's standard formatting.
     *
     * @param exception the user-facing error.
     */
    public void showError(TurtleyException exception) {
        hasError = true;
        showSeparator();
        output.println(" " + exception.getMessage() + " o/T\\>");
        showSeparator();
    }

    /**
     * Displays the message used when the task list is full.
     */
    public void showTaskListFull() {
        hasError = true;
        showSeparator();
        output.println("Task list full, do some work you lazy bum! o/T\\>");
        showSeparator();
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task the added task.
     * @param taskCount the resulting number of tasks.
     */
    public void showTaskAdded(Task task, int taskCount) {
        showSeparator();
        output.println("Got it. I've added this task:");
        output.println("  " + task);
        output.println("Now you have " + taskCount + " tasks in the list.");
        showSeparator();
    }

    /**
     * Displays all tasks, or the empty-list message when there are none.
     *
     * @param tasks the tasks to display in list order.
     */
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            showSeparator();
            output.println("Task list empty. Good job! Here's a cookie. o/T\\>");
            showSeparator();
            return;
        }

        showSeparator();
        output.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            showTask(i, tasks.get(i));
        }
        showSeparator();
    }

    /**
     * Displays one numbered task.
     *
     * @param index the zero-based task index.
     * @param task the task to display.
     */
    public void showTask(int index, Task task) {
        output.println(" " + (index + 1) + "." + task);
    }

    /**
     * Displays the header for a timecheck result.
     *
     * @param cutoffText the formatted cutoff date/time.
     */
    public void showTimecheckHeader(String cutoffText) {
        showSeparator();
        output.println(" Here are the deadline and event tasks on or before " + cutoffText + ":");
    }

    /**
     * Displays the header for a find result.
     */
    public void showFindHeader() {
        showSeparator();
        output.println(" Here are the matching tasks in your list:");
    }

    /**
     * Displays the header for a tag-filter result.
     *
     * @param query the tag substring used for filtering.
     */
    public void showFilterHeader(String query) {
        showSeparator();
        output.println(" Here are the tasks with tags matching " + query + ":");
    }

    /**
     * Displays the command reference.
     */
    public void showHelp() {
        showSeparator();
        output.println("Commands:");
        output.println("todo <description> [/tag #tag1 #tag2 ...]");
        output.println("deadline <description> /by <date> [/tag #tag1 #tag2 ...]");
        output.println("event <description> /from <start> /to <end> [/tag #tag1 #tag2 ...]");
        output.println("tag <task number> #tag1 [#tag2 ...]");
        output.println("untag <task number> #tag1 [#tag2 ...]");
        output.println("filter #tag-substring");
        output.println("list");
        output.println("find <keyword>");
        output.println("mark <task number>");
        output.println("unmark <task number>");
        output.println("delete <task number>");
        output.println("timecheck <date/time>");
        output.println("help");
        output.println("bye");
        output.println();
        output.println("Tags must start with # and contain 1-10 non-whitespace characters.");
        output.println("Each task can have at most 10 tags.");
        output.println("Tags are case-sensitive, displayed alphabetically, and searched case-insensitively.");
        showSeparator();
    }

    /**
     * Displays confirmation that tags were added to a task.
     *
     * @param task the tagged task.
     */
    public void showTaskTagged(Task task) {
        showSeparator();
        output.println(" Got it. I've tagged this task:");
        output.println("   " + task);
        showSeparator();
    }

    /**
     * Displays confirmation that tags were removed from a task.
     *
     * @param task the untagged task.
     */
    public void showTaskUntagged(Task task) {
        showSeparator();
        output.println(" OK, I've untagged this task:");
        output.println("   " + task);
        showSeparator();
    }

    /**
     * Displays the result used when timecheck finds no matching tasks.
     */
    public void showNoMatchingTasks() {
        output.println("None! o/T\\>");
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task the completed task.
     */
    public void showTaskMarked(Task task) {
        showSeparator();
        output.println(" Nice! I've marked this task as done:");
        output.println("   [" + task.getStatusIcon() + "] " + task.getDescriptionWithTags());
        showSeparator();
    }

    /**
     * Displays confirmation that a task was marked as not done.
     *
     * @param task the task whose completion was removed.
     */
    public void showTaskUnmarked(Task task) {
        showSeparator();
        output.println(" OK, I've marked this task as not done yet:");
        output.println("   [" + task.getStatusIcon() + "] " + task.getDescriptionWithTags());
        showSeparator();
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task the deleted task.
     * @param taskCount the resulting number of tasks.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showSeparator();
        output.println(" Noted. I've removed this task:");
        output.println("   " + task);
        output.println(" Now you have " + taskCount + " tasks in the list.");
        showSeparator();
    }

    /**
     * Displays the response for an empty input line.
     */
    public void showEmptyInput() {
        hasError = true;
        output.println("Please input something. o/T\\>");
        showSeparator();
    }

    /**
     * Displays the response for an unrecognized command.
     */
    public void showUnknownCommand() {
        hasError = true;
        output.println("Please input something correct. o/T\\>");
        showSeparator();
    }

    /**
     * Displays the standard separator line.
     */
    public void showSeparator() {
        if (shouldShowSeparators) {
            output.println(SEPARATOR);
        }
    }

    /**
     * Returns whether this UI has displayed an error during its response.
     *
     * @return whether an error was displayed.
     */
    public boolean hasError() {
        return hasError;
    }
}
