package sage.ui;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Consumer;

import sage.task.Note;
import sage.task.Task;

/**
 * Reads console commands and sends Sage messages to a configured output destination.
 */
public class Ui {
    /**
     * Separates command responses in the console.
     */
    public static final String LINE = "____________________________________________________________";

    /**
     * Greets users consistently in the console and graphical interfaces.
     */
    public static final String WELCOME_MESSAGE = "Hello, I'm Sage.\nA little clarity, one step at a time.\n"
            + "Try todo <task>, note <text>, or list.";

    /**
     * Reads console commands, or is null when this interface is configured for output only.
     */
    private final Scanner scanner;

    /**
     * Receives each line produced by the display methods.
     */
    private final Consumer<String> output;

    /**
     * Creates a user interface that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
        output = line -> System.out.println(line);
    }

    /**
     * Creates a user interface that sends messages to the given output consumer.
     * This output-only interface does not read commands from standard input.
     *
     * @param output The consumer that receives each line of user-facing output.
     */
    public Ui(Consumer<String> output) {
        scanner = null;
        this.output = Objects.requireNonNull(output);
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return The next line from standard input, or null when input has ended.
     * @throws IllegalStateException If this interface was created for output only.
     */
    public String readCommand() {
        if (scanner == null) {
            throw new IllegalStateException("This user interface does not accept command input.");
        }
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /**
     * Shows the Sage banner and initial greeting.
     */
    public void showWelcome() {
        String banner = "  ____                       \n"
                + " / ___|  __ _  __ _  ___     \n"
                + " \\___ \\ / _` |/ _` |/ _ \\    \n"
                + "  ___) | (_| | (_| |  __/    \n"
                + " |____/ \\__,_|\\__, |\\___|    \n"
                + "              |___/          \n";
        output.accept(LINE);
        output.accept(banner);
        WELCOME_MESSAGE.lines().forEach(output);
        output.accept(LINE);
    }

    /**
     * Shows the separator used between command responses.
     */
    public void showLine() {
        output.accept(LINE);
    }

    /**
     * Shows the farewell message.
     */
    public void showBye() {
        output.accept("Take care. One step at a time.");
    }

    /**
     * Shows all tasks in their current list order.
     *
     * @param tasks The tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            output.accept("A clear page. Add a task with todo <task> or a note with note <text>.");
            output.accept(LINE);
            return;
        }
        output.accept("Here's your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.accept((i + 1) + ". " + tasks.get(i));
        }
        output.accept(LINE);
    }

    /**
     * Shows tasks matching a search, or a message when no tasks match.
     *
     * @param tasks The matching tasks to display.
     * @param allTasks The full list whose task numbers must be retained in search results.
     */
    public void showMatchingTasks(List<Task> tasks, List<Task> allTasks) {
        if (tasks.isEmpty()) {
            output.accept("No matches this time. Try another word from the description.");
            output.accept(LINE);
            return;
        }

        output.accept("Here's what I found:");
        for (Task task : tasks) {
            int taskIndex = allTasks.indexOf(task);
            // Search results must refer to entries in the same list used by mutation commands.
            assert taskIndex >= 0 : "A matching task must belong to the complete task list";
            output.accept((taskIndex + 1) + ". " + task);
        }
        output.accept(LINE);
    }

    /**
     * Prints information after a task has been added.
     *
     * @param task The newly added task.
     * @param taskCount The total number of tasks after the addition.
     */
    public void showAddedTask(Task task, int taskCount) {
        if (task instanceof Note) {
            output.accept("Saved for later:");
            output.accept("  " + task);
            return;
        }
        output.accept("Noted. One less thing to remember:");
        output.accept("  " + task);
        showItemCount(taskCount);
    }

    /**
     * Shows the removed task and the number of remaining tasks.
     *
     * @param task The task that was removed.
     * @param taskCount The number of tasks remaining after removal.
     */
    public void showRemovedTask(Task task, int taskCount) {
        if (task instanceof Note) {
            output.accept("Note cleared:");
            output.accept("  " + task);
            return;
        }
        output.accept("Cleared from your list:");
        output.accept("  " + task);
        showItemCount(taskCount);
    }

    /**
     * Counts tasks and notes together with the correct singular or plural form.
     *
     * @param itemCount The total number of entries in the list.
     */
    private void showItemCount(int itemCount) {
        output.accept(itemCount + (itemCount == 1 ? " item" : " items") + " in your list.");
    }

    /**
     * Shows confirmation that a task was marked as done.
     *
     * @param task The task that was marked as done.
     */
    public void showMarkedDone(Task task) {
        output.accept("A little progress. Marked as done:");
        output.accept("  " + task);
    }

    /**
     * Shows confirmation that a task was marked as not done.
     *
     * @param task The task that was marked as not done.
     */
    public void showMarkedUndone(Task task) {
        output.accept("Back on your list. Marked as not done:");
        output.accept("  " + task);
    }

    /**
     * Shows a user-facing error message followed by a separator.
     *
     * @param message The error details to display.
     */
    public void showError(String message) {
        output.accept("Let's try that again. " + message);
        output.accept(LINE);
    }
}
