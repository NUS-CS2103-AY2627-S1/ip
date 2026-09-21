package topaz.ui;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import topaz.TopazException;
import topaz.task.Task;
import topaz.task.TaskList;

/**
 * Handles console input and all messages shown to the user.
 */
public class Ui {
    public static final String NAME = "Gronk";
    public static final String CATCHPHRASE = "GRONK!";
    public static final String WELCOME_MESSAGE = "I'm Gronk, your mighty task keeper.\n"
            + "Give Gronk a task. We crush it together!";

    private static final String SEPARATOR = "____________________________________________________________";

    private final Scanner scanner;
    private final Consumer<String> output;

    /**
     * Creates a UI that reads commands from the standard input stream.
     */
    public Ui() {
        scanner = new Scanner(System.in);
        output = System.out::println;
    }

    /**
     * Creates a UI that appends messages to the given response.
     *
     * @param response the response that receives UI messages
     */
    public Ui(StringBuilder response) {
        scanner = null;
        output = message -> response.append(message).append(System.lineSeparator());
    }

    /**
     * Returns whether another command is available from the user.
     */
    public boolean hasNextCommand() {
        if (scanner == null) {
            throw new IllegalStateException("This UI does not read console commands.");
        }
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command from the user.
     */
    public String readCommand() {
        if (scanner == null) {
            throw new IllegalStateException("This UI does not read console commands.");
        }
        return scanner.nextLine();
    }

    /**
     * Shows the application greeting.
     */
    public void showWelcome() {
        output.accept(SEPARATOR);
        showResponse(WELCOME_MESSAGE);
        output.accept(SEPARATOR);
    }

    /**
     * Shows an error that prevented Gronk from starting.
     *
     * @param exception the startup error to display
     */
    public void showLoadingError(TopazException exception) {
        showResponse(" Gronk hit a snag. " + exception.getMessage());
    }

    /**
     * Shows every task in the task list.
     *
     * @param tasks the task list to display
     */
    public void showTaskList(TaskList tasks) {
        showResponse(" Gronk guards your task pile:");
        if (tasks.size() == 0) {
            output.accept(" No tasks yet. Try: todo read book");
        }
        showNumberedTasks(tasks.asList());
    }

    /**
     * Shows tasks whose descriptions match a search keyword.
     *
     * @param matchingTasks the matching tasks to display
     * @param allTasks the full list that supplies the numbers used by task commands
     */
    public void showMatchingTasks(List<Task> matchingTasks, TaskList allTasks) {
        showResponse(" Gronk sniffed out these tasks:");
        if (matchingTasks.isEmpty()) {
            output.accept(" No matching tasks. Try another keyword or use list.");
        }
        for (Task task : matchingTasks) {
            showNumberedTask(task, allTasks.asList().indexOf(task) + 1);
        }
    }

    /**
     * Shows the supplied tasks with one-based numbers.
     *
     * @param tasks the tasks to display
     */
    private void showNumberedTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            showNumberedTask(tasks.get(i), i + 1);
        }
    }

    /**
     * Shows a task with the number accepted by mark, unmark, and delete.
     */
    private void showNumberedTask(Task task, int taskNumber) {
        output.accept(" " + taskNumber + "." + task.getDisplayIcon() + " " + task.getDescription());
    }

    /**
     * Shows a newly added task.
     *
     * @param task the task that was added
     * @param taskCount the number of tasks after adding the task
     */
    public void showAddedTask(Task task, int taskCount) {
        showResponse(" Gronk grabbed a new task:");
        output.accept("   " + task.getDisplayIcon() + " " + task.getDescription());
        output.accept(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows a task marked as done.
     *
     * @param task the task that was marked as done
     */
    public void showMarkedTask(Task task) {
        showResponse(" Task crushed! Gronk marks it done:");
        output.accept("   " + task.getDisplayIcon() + " " + task.getDescription());
    }

    /**
     * Shows a task marked as not done.
     *
     * @param task the task that was marked as not done
     */
    public void showUnmarkedTask(Task task) {
        showResponse(" Back to the pile! Gronk marks it not done:");
        output.accept("   " + task.getDisplayIcon() + " " + task.getDescription());
    }

    /**
     * Shows a task removed from the list.
     *
     * @param task the task that was removed
     * @param taskCount the number of tasks remaining after removal
     */
    public void showDeletedTask(Task task, int taskCount) {
        showResponse(" Gronk tossed this task out:");
        output.accept("   " + task.getDisplayIcon() + " " + task.getDescription());
        output.accept(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows an error caused by a command.
     *
     * @param exception the command error to display
     */
    public void showError(TopazException exception) {
        showResponse(" Gronk hit a snag. " + exception.getMessage());
    }

    /**
     * Announces a response with Gronk's signature battle cry.
     *
     * @param message the response body, including any useful command guidance
     */
    private void showResponse(String message) {
        output.accept(CATCHPHRASE);
        output.accept(message);
    }

    /**
     * Shows the standard separator below a response.
     */
    public void showSeparator() {
        output.accept(SEPARATOR);
    }

    /**
     * Shows the goodbye message and closing separator.
     */
    public void showGoodbye() {
        showResponse(" Gronk rests now. Come back strong!");
        showSeparator();
    }
}
