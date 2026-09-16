package anaconda.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import anaconda.task.Task;

/**
 * Handles console input and all messages displayed by the chatbot.
 */
public class Ui implements AutoCloseable {
    public static final String LOADING_ERROR_MESSAGE =
            "Your saved list was compromised or could not be read. Starting with a new empty list.";
    private static final String LINE = "____________________________________________________________";

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a UI using the current console input and output streams.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI with explicit input and output streams.
     * Closing this UI closes its input reader, but leaves the supplied output open.
     *
     * @param input Input read by this UI.
     * @param output Destination for messages, owned by the caller.
     */
    public Ui(InputStream input, PrintStream output) {
        scanner = new Scanner(input);
        this.output = output;
    }

    /**
     * Displays the welcome banner and initial prompt.
     */
    public void showWelcome() {
        String banner =
                "    _    _   _    _    ____ ___  _   _ ____    _\n"
                        + "   / \\  | \\ | |  / \\  / ___/ _ \\| \\ | |  _ \\  / \\\n"
                        + "  / _ \\ |  \\| | / _ \\| |  | | | |  \\| | | | |/ _ \\\n"
                        + " / ___ \\| |\\  |/ ___ \\ |__| |_| | |\\  | |_| / ___ \\\n"
                        + "/_/   \\_\\_| \\_/_/   \\_\\____\\___/|_| \\_|____/_/   \\_\\\n";

        showToUser(
                LINE,
                banner,
                "Yo, it's Anaconda.",
                "What do you need?");
    }

    /**
     * Reads and trims the next line entered by the user.
     *
     * @return Complete command text.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays a separator around a response.
     */
    public void showLine() {
        showToUser(LINE);
    }

    /**
     * Displays a user-facing error.
     *
     * @param message Explanation of the error.
     */
    public void showError(String message) {
        showToUser("Hang on. " + message);
    }

    /**
     * Lists available commands in compact groups for users whose input was not recognized.
     */
    public void showCommandList() {
        showToUser("Available commands:",
                "Add: todo, deadline, event",
                "View/search: list, find, /by, /from",
                "Update: mark, unmark, delete, clear",
                "History: undo, undo undo (redo)",
                "Help: help",
                "Exit: bye");
    }

    /**
     * Reports that previously saved tasks could not be loaded.
     */
    public void showLoadingError() {
        showError(LOADING_ERROR_MESSAGE);
    }

    /**
     * Displays a numbered full list or filtered search result.
     *
     * @param tasks Tasks to display in their current order.
     * @param isFiltered Whether these tasks are a search result.
     */
    public void showTasks(List<Task> tasks, boolean isFiltered) {
        if (isFiltered) {
            showFindResults(tasks);
            return;
        }
        showToUser("Here's what you've got:");
        showNumberedTasks(tasks);
    }

    /**
     * Displays tasks whose descriptions match a find command.
     *
     * @param tasks Matching tasks in their original order.
     */
    public void showFindResults(List<Task> tasks) {
        showToUser(tasks.isEmpty() ? "Nothing. No matching tasks." : "Found these:");
        showNumberedTasks(tasks);
    }

    /**
     * Displays task rows with consecutive one-based numbers for every list view.
     */
    private void showNumberedTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            showToUser((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays an added task and the updated task count.
     *
     * @param task Task that was added.
     * @param taskCount Number of tasks now stored.
     */
    public void showTaskAdded(Task task, int taskCount) {
        showToUser(
                "Alright, added it:",
                "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Warns that the newly added task duplicates an existing task and explains how to undo it.
     */
    public void showDuplicateWarning() {
        showToUser("Did you forget? This task already exists.",
                "We can undo anyways. . .");
    }

    /**
     * Displays a removed task and the updated task count.
     *
     * @param task Task that was removed.
     * @param taskCount Number of tasks remaining.
     */
    public void showTaskRemoved(Task task, int taskCount) {
        showToUser(
                "Gone. Hope you didn't need that:",
                "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays a task after changing its completion status.
     *
     * @param task Task that was updated.
     * @param isDone Whether the task was marked as completed.
     */
    public void showMarked(Task task, boolean isDone) {
        showToUser(
                isDone ? "Fine, that's done now:" : "Really? Unmarked? Alright . . .",
                "  " + task);
    }

    /**
     * Reports that the task list was cleared.
     */
    public void showCleared() {
        showToUser("Fine. Everything's gone.");
    }

    /**
     * Reports that the previous task-changing command was undone and saved.
     */
    public void showUndo() {
        showToUser("Undid the previous command.");
    }

    /**
     * Reports that the previous undo was reversed and saved.
     */
    public void showRedo() {
        showToUser("Undid the previous undo.");
    }

    /**
     * Displays the farewell message.
     */
    public void showGoodbye() {
        showToUser(LINE, "Alright, off you go. Try to get something done.", LINE);
    }

    /**
     * Displays each supplied message on a separate line.
     *
     * @param messages Messages to display in order.
     */
    private void showToUser(String... messages) {
        for (String message : messages) {
            output.println(message);
        }
    }

    /**
     * Closes the console input reader when the chatbot finishes.
     */
    @Override
    public void close() {
        scanner.close();
    }
}
