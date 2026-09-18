package luna.ui;

import java.util.List;
import java.util.Scanner;

import luna.task.Task;
import luna.task.TaskList;

/**
 * Handles user-facing messages in Luna's calm night-shift voice.
 */
public class Ui {
    private static final String DIVIDER_LINE = "   _________________________________________________________________";
    private static final String BANNER_TEXT = " _\n"
            + "| |    _   _ _ __   __ _\n"
            + "| |   | | | | '_ \\ / _` |\n"
            + "| |___| |_| | | | | (_| |\n"
            + "|_____|\\__,_|_| |_|\\__,_|";
    private static final String WELCOME_TEXT = BANNER_TEXT
            + "\n\nHi, I'm Luna.\nI keep your tasks organized after dark."
            + "\nType help to view all commands.";
    private static final String GUI_WELCOME_TEXT = "Plan your night\n"
            + "Add tasks, track deadlines, and keep completed work out of the way.\n\n"
            + "Type help to view all commands.";
    private static final String AVAILABLE_COMMANDS = "Available commands:\n"
            + "> todo <desc>: Adds a todo task with the given description\n"
            + "> deadline <desc> /by <yyyy-MM-dd>: Adds a deadline task with the given due date\n"
            + "> event <desc> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>: "
            + "Adds a task that spans across a specific time\n"
            + "> list: Displays all currently saved items with index numbers\n"
            + "> list archived: Displays all archived tasks with index numbers\n"
            + "> find <keyword>: Displays tasks whose descriptions contain the keyword\n"
            + "> mark <index>: Marks the task at the specified index number as completed ([X]).\n"
            + "> unmark <index>: Marks the task at the specified index number as not done ([ ]).\n"
            + "> delete <index>: Removes the task at the specified index number from the list.\n"
            + "> archive <index>: Moves the task at the specified index into the archive.\n"
            + "> archive all: Moves every task into the archive.\n"
            + "> help: Displays this command guide.\n"
            + "> bye: Exits the program";
    private static final String EXIT_TEXT = "The moon is setting. Rest well - I'll keep your tasks safe.";
    private final Scanner scanner;
    private final boolean shouldPrintToConsole;
    private String latestResponse;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        this(true);
    }

    /**
     * Creates a UI that can optionally print responses to the console.
     *
     * @param shouldPrintToConsole Whether responses should be printed immediately.
     */
    public Ui(boolean shouldPrintToConsole) {
        this.shouldPrintToConsole = shouldPrintToConsole;
        this.scanner = shouldPrintToConsole ? new Scanner(System.in) : null;
        this.latestResponse = "";
    }

    /**
     * Shows a brief welcome message.
     */
    public void showWelcome() {
        showMessage(getWelcomeMessage());
    }

    /**
     * Returns the compact welcome text shown when Luna starts.
     *
     * @return Welcome message shown when Luna starts.
     */
    public String getWelcomeMessage() {
        if (shouldPrintToConsole) {
            return DIVIDER_LINE + '\n'
                    + "   " + WELCOME_TEXT.replace("\n", "\n   ") + '\n'
                    + DIVIDER_LINE + '\n';
        }

        return GUI_WELCOME_TEXT;
    }

    /**
     * Shows the complete command reference.
     */
    public void showHelp() {
        showMessage(formatResponse(AVAILABLE_COMMANDS));
    }

    /**
     * Reads one line of user input.
     *
     * @return User input with leading and trailing whitespace removed.
     */
    public String readCommand() {
        if (scanner == null) {
            throw new IllegalStateException("This UI is not configured for command-line input.");
        }

        System.out.print("> ");
        if (!scanner.hasNextLine()) {
            return "bye";
        }
        return scanner.nextLine().trim();
    }

    /**
     * Shows the exit message.
     */
    public void showExit() {
        showMessage(formatResponse(EXIT_TEXT));
    }

    /**
     * Shows the current list of tasks.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        if (tasks.size() == 0) {
            showMessage(formatResponse("Your radar is clear - there are no active tasks yet.\n"
                    + "Try todo <description> to add one."));
            return;
        }

        showTasks("Here's what's on your radar:", tasks.asList());
    }

    /**
     * Shows the tasks whose descriptions match a search keyword.
     *
     * @param tasks Complete active task list.
     * @param matchingTaskIndices Zero-based indices of tasks that matched the search keyword.
     */
    public void showMatchingTasks(TaskList tasks, List<Integer> matchingTaskIndices) {
        StringBuilder message = new StringBuilder("These tasks came into view:");
        for (int taskIndex : matchingTaskIndices) {
            message.append('\n').append(taskIndex + 1).append(". ").append(tasks.get(taskIndex));
        }
        showMessage(formatResponse(message.toString()));
    }

    /**
     * Shows all tasks currently stored in the archive.
     *
     * @param archivedTasks Archived tasks to display.
     */
    public void showArchivedTasks(List<Task> archivedTasks) {
        if (archivedTasks.isEmpty()) {
            showMessage(formatResponse("Your archive is quiet - nothing is resting there yet."));
            return;
        }

        showTasks("These tasks are resting in your archive:", archivedTasks);
    }

    /**
     * Shows a collection of tasks under the given heading.
     *
     * @param heading Heading shown before the task entries.
     * @param tasks Tasks to display.
     */
    private void showTasks(String heading, List<Task> tasks) {
        StringBuilder message = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            message.append('\n').append(i + 1).append(". ").append(tasks.get(i));
        }
        showMessage(formatResponse(message.toString()));
    }

    /**
     * Shows a confirmation for a task that was marked as done.
     *
     * @param task Task that was marked.
     */
    public void showMarkSuccess(Task task) {
        showMessage(formatResponse("Nicely done - this task is complete:\n" + task));
    }

    /**
     * Shows a confirmation for a task that was marked as not done.
     *
     * @param task Task that was unmarked.
     */
    public void showUnmarkSuccess(Task task) {
        showMessage(formatResponse("No rush. This task is back on your active path:\n" + task));
    }

    /**
     * Shows a confirmation for a task that was added.
     *
     * @param task Added task.
     * @param taskCount Current number of tasks in the list.
     */
    public void showAddSuccess(Task task, int taskCount) {
        showTaskCountChange("It's on your radar", task, taskCount);
    }

    /**
     * Shows a confirmation for a task that was deleted.
     *
     * @param task Removed task.
     * @param taskCount Current number of tasks remaining in the list.
     */
    public void showDeleteSuccess(Task task, int taskCount) {
        showTaskCountChange("Cleared from your path", task, taskCount);
    }

    /**
     * Shows a confirmation after tasks have been archived.
     *
     * @param archivedTaskCount Number of tasks moved into the archive.
     * @param activeTaskCount Number of tasks remaining in the active list.
     */
    public void showArchiveSuccess(int archivedTaskCount, int activeTaskCount) {
        String archivedTaskNoun = archivedTaskCount == 1 ? "task" : "tasks";
        String activeTaskNoun = activeTaskCount == 1 ? "task" : "tasks";
        showMessage(formatResponse("Tucked away " + archivedTaskCount + " " + archivedTaskNoun
                + " to data/archive.txt.\n"
                + "You have " + activeTaskCount + " " + activeTaskNoun + " still on your radar."));
    }

    /**
     * Shows that an archive-all request had no active tasks to archive.
     */
    public void showNoTasksToArchive() {
        showMessage(formatResponse("Your active list is already clear - there is nothing to archive."));
    }

    /**
     * Shows an error message to the user.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        showMessage(formatResponse("I lost that signal. " + message));
    }

    /**
     * Shows a loading error when the saved task file cannot be read.
     */
    public void showLoadingError() {
        showError("I could not load your saved tasks. You can still view this session, but changes are disabled "
                + "to protect the existing file.");
    }

    /**
     * Shows a confirmation for a task-count change.
     *
     * @param actionMessage Message describing the task action.
     * @param task Task affected by the action.
     * @param taskCount Current number of tasks in the list.
     */
    private void showTaskCountChange(String actionMessage, Task task, int taskCount) {
        String taskNoun = taskCount == 1 ? "task" : "tasks";
        showMessage(formatResponse(actionMessage + ":\n"
                + task + '\n'
                + "You now have " + taskCount + " " + taskNoun + " on your radar."));
    }

    /**
     * Closes the UI input resource.
     */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }

    /**
     * Returns the most recent response and clears it from the buffer.
     *
     * @return Latest response text, or an empty string if none exists.
     */
    public String consumeLatestResponse() {
        String response = latestResponse;
        latestResponse = "";
        return response;
    }

    /**
     * Stores a response and optionally prints it to the console.
     *
     * @param message Response to show.
     */
    private void showMessage(String message) {
        latestResponse = message;
        if (shouldPrintToConsole) {
            System.out.print(message);
        }
    }

    /**
     * Formats a response for the active presentation mode.
     *
     * @param content Message content without CLI-specific decoration.
     * @return Formatted response text.
     */
    private String formatResponse(String content) {
        if (!shouldPrintToConsole) {
            return content;
        }

        String indentedContent = content.replace("\n", "\n      ");
        return DIVIDER_LINE + '\n'
                + "      " + indentedContent + '\n'
                + DIVIDER_LINE + '\n';
    }
}
