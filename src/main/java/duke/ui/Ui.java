package duke.ui;

import java.util.ArrayList;
import java.util.stream.IntStream;

import duke.task.Task;

/**
 * Handles all user-visible output for the Duke application.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String WELCOME_MESSAGE = "HELLO! I'm GARY!\n"
            + "How can I help you today?\n"
            + "(Type bye to exit)";

    /**
     * Returns the greeting shown when the application starts.
     *
     * @return Welcome message without the terminal-only banner.
     */
    public String getWelcomeMessage() {
        return WELCOME_MESSAGE;
    }

    /**
     * Shows the welcome banner and greeting message.
     */
    public void showWelcome() {
        String banner = """
                 ==================================================================\s
                  ____                 \s
                 / ___| __ _ _ __ _   _\s
                | |  _ / _` | '__| | | |
                | |_| | (_| | |  | |_| |
                 \\____|\\__,_|_|   \\__, |
                                  |___/\s
                 ==================================================================\s
                """;
        System.out.println(banner + WELCOME_MESSAGE + System.lineSeparator());
    }

    /**
     * Shows the goodbye message.
     */
    public void showGoodbye() {
        System.out.println("Bye! Hope to see you again soon!\n");
    }

    /**
     * Shows a horizontal separator line.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Shows an error message indicating that the user did not enter a command.
     */
    public void showEmptyInputError() {
        showError("Please enter a command.");
    }

    /**
     * Shows a generic informational message.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Shows an error message with separator lines around it.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        showLine();
        System.out.println(message);
        showLine();
    }

    /**
     * Shows a confirmation message after a task is added.
     *
     * @param task The task that was added.
     * @param taskCount Total number of tasks after adding.
     */
    public void showTaskAdded(Task task, int taskCount) {
        showLine();
        System.out.println("Got it! Here's the task you added:\n"
                + "  " + task
                + "\nNow you have " + taskCount + " tasks in your list!");
        showLine();
    }

    /**
     * Shows a confirmation message after a task is removed.
     *
     * @param task The task that was removed.
     * @param taskCount Total number of tasks after removal.
     */
    public void showTaskRemoved(Task task, int taskCount) {
        showLine();
        System.out.println("Noted. This task has been removed:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        showLine();
    }

    /**
     * Shows a message after a task is marked or unmarked.
     *
     * @param taskNumber 1-based task number.
     * @param isDone Whether the task is now marked as done.
     */
    public void showMarkedTask(int taskNumber, boolean isDone) {
        showLine();
        System.out.println("Gary marked task " + taskNumber + (isDone ? " as done!" : " as undone!"));
        showLine();
    }

    /**
     * Shows all tasks in the task list.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(ArrayList<Task> tasks) {
        showLine();
        System.out.println("These are the tasks you have in your list!");
        IntStream.range(0, tasks.size())
                .forEach(index -> System.out.println((index + 1) + ". " + tasks.get(index)));
        showLine();
    }

    /**
     * Shows tasks matching a keyword search.
     *
     * @param tasks Matching tasks to display.
     */
    public void showMatchingTasks(ArrayList<Task> tasks) {
        showLine();

        if (tasks.isEmpty()) {
            System.out.println("No matching tasks found.");
            showLine();
            return;
        }

        System.out.println("Here are the matching tasks in your list:");
        IntStream.range(0, tasks.size())
                .forEach(index -> System.out.println((index + 1) + ". " + tasks.get(index)));
        showLine();
    }
}
