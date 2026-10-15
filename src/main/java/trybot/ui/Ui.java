package trybot.ui;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import trybot.task.Task;
import trybot.task.TaskList;

/**
 * Handles all console interactions with the TryBot user.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String BANNER = " _____             ____        _\n"
            + "|_   _| _ __ _   _ | __ )  ___ | |_\n"
            + "  | |  | '__| | | ||  _ \\ / _ \\| __|\n"
            + "  | |  | |  | |_| || |_) | (_) | |_\n"
            + "  |_|  |_|   \\__, ||____/ \\___/ \\__|\n"
            + "              |___/\n";
    private final Scanner scanner = new Scanner(System.in);
    private final PrintStream output;
    private final PrintStream errorOutput;

    /** Creates a UI that reads from standard input and writes to standard output. */
    public Ui() {
        this(System.out, System.err);
    }

    /**
     * Creates a UI that writes messages to the supplied streams.
     *
     * @param output stream for normal messages.
     * @param errorOutput stream for warnings.
     */
    public Ui(PrintStream output, PrintStream errorOutput) {
        this.output = output;
        this.errorOutput = errorOutput;
    }

    /**
     * Displays TryBot's welcome message.
     */
    public void showWelcome() {
        showLine();
        output.print(BANNER);
        output.println("Hello! I'm TryBot.");
        output.println("What can I do for you?");
        showLine();
    }

    /**
     * Displays the standard separator used between console interactions.
     */
    public void showLine() {
        output.println(SEPARATOR);
    }

    /**
     * Checks whether another command is available from the user.
     *
     * @return true when another input line can be read
     */
    public boolean hasMoreCommands() {
        return scanner.hasNextLine();
    }

    /**
     * Reads one complete command from the user.
     *
     * @return raw command text
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays TryBot's goodbye message.
     */
    public void showGoodbye() {
        showGoodbyeMessage();
        showLine();
    }

    /**
     * Displays only TryBot's goodbye message, without a separator.
     *
     * The command loop displays the final separator in its {@code finally} block.
     */
    public void showGoodbyeMessage() {
        output.println("Bye. Hope to see you again soon!");
    }

    /**
     * Displays an input or task-management error.
     *
     * @param message user-friendly error message.
     */
    public void showError(String message) {
        output.println(message);
    }

    /**
     * Displays a confirmation after adding a task.
     *
     * @param task task that was added.
     * @param taskCount number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println("Got it. I've added this task:");
        output.println(task);
        output.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays all tasks in their current list order.
     *
     * @param tasks tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        output.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays tasks matching a search keyword in their original task-list order.
     *
     * @param matchingTasks tasks whose descriptions matched the search.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            output.println("No tasks match that keyword.");
            return;
        }

        output.println("Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            output.println((i + 1) + "." + matchingTasks.get(i));
        }
    }

    /**
     * Displays a confirmation after marking a task as done.
     *
     * @param task task that was marked as done.
     */
    public void showTaskMarkedDone(Task task) {
        output.println("Good work!! I've marked this task as done:");
        output.println(task);
    }

    /**
     * Displays a confirmation after marking a task as not done.
     *
     * @param task task that was marked as not done.
     */
    public void showTaskMarkedNotDone(Task task) {
        output.println("OK, I've marked this task as not done yet:");
        output.println(task);
    }

    /**
     * Displays a confirmation after deleting a task.
     *
     * @param task task that was deleted.
     * @param taskCount number of tasks after the deletion.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println("Noted. I've removed this task:");
        output.println(task);
        output.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays a confirmation after attaching a tag.
     *
     * @param taskNumber one-based task number.
     * @param tagName tag that was attached.
     */
    public void showTagCreated(int taskNumber, String tagName) {
        output.println("Tag Created for Task " + taskNumber + ": " + tagName);
    }

    /**
     * Displays a confirmation after removing tags from a task.
     *
     * @param taskNumber one-based task number.
     */
    public void showTagsDeleted(int taskNumber) {
        output.println("Tags Deleted for Task " + taskNumber);
    }

    /** Displays the commands available in TryBot and explains their usage. */
    public void showHelp() {
        output.println("Here are the commands you can use:");
        output.println("help: help (shows this list of commands)");
        output.println("todo: todo <task> (adds a todo task to the list)");
        output.println("deadline: deadline <task> /by <date or time> (adds a deadline task)");
        output.println("event: event <task> /from <start> /to <end> (adds an event task)");
        output.println("list: list (shows all tasks)");
        output.println("find: find <keyword> (finds tasks containing the keyword)");
        output.println("tag: tag <number> <tag name> (attaches a tag to a task)");
        output.println("tagdel: tagdel <number> (removes all tags from a task)");
        output.println("mark: mark <number> (marks a task as done)");
        output.println("unmark: unmark <number> (marks a task as not done)");
        output.println("delete: delete <number> (deletes a task)");
        output.println("bye: bye (exits TryBot)");
    }

    /**
     * Reports that the saved task list could not be loaded.
     */
    public void showLoadingError() {
        errorOutput.println("Warning: TryBot could not load the saved task list. "
                + "Starting with an empty list.");
    }

    /**
     * Reports that the current task list could not be saved.
     */
    public void showSavingError() {
        errorOutput.println("Warning: TryBot could not save the task list. "
                + "Your changes will be lost when TryBot exits.");
    }
}
