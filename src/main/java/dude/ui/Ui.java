package dude.ui;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import dude.command.core.CommandType;
import dude.command.duplicate.DuplicateTaskConflict;
import dude.exception.UsageException;
import dude.task.Task;
import dude.task.TaskMatch;

/**
 * Coordinates console input and user-facing output adapters.
 */
public class Ui {
    private final ConsoleInput input;
    private final ConsoleRenderer renderer;

    /**
     * Creates a UI connected to standard input and output.
     */
    public Ui() {
        this(new Scanner(System.in), System.out);
    }

    /**
     * Creates a UI connected to a supplied scanner and standard output.
     *
     * @param scanner Source of user commands.
     */
    public Ui(Scanner scanner) {
        this(scanner, System.out);
    }

    /**
     * Creates a UI connected to supplied input and output streams.
     *
     * @param scanner Source of user commands.
     * @param output  Destination for user-facing output.
     */
    public Ui(Scanner scanner, PrintStream output) {
        input = new ConsoleInput(Objects.requireNonNull(scanner, "scanner"));
        renderer = new ConsoleRenderer(Objects.requireNonNull(output, "output"));
    }

    /**
     * Displays the welcome banner and supported date formats.
     */
    public void showWelcome() {
        renderer.showWelcome();
    }

    /**
     * Returns whether another input line is available.
     *
     * @return True when input remains.
     */
    public boolean hasNextCommand() {
        return input.hasNextLine();
    }

    /**
     * Reads the next non-blank command.
     *
     * @return Raw command, or null at end of input.
     */
    public String readCommand() {
        return input.readCommand();
    }

    /**
     * Reads one raw input line, including a blank line.
     *
     * @return Input line, or null at end of input.
     */
    public String readInputLine() {
        return input.readInputLine();
    }

    /**
     * Displays one divider line.
     */
    public void showLine() {
        renderer.showLine();
    }

    /**
     * Displays the standard goodbye message.
     */
    public void showGoodbye() {
        renderer.showGoodbye();
    }

    /**
     * Displays all tasks with one-based positions.
     *
     * @param tasks Application task list.
     */
    public void showTaskList(List<Task> tasks) {
        renderer.showTaskList(tasks);
    }

    /**
     * Displays tasks whose descriptions contain a keyword.
     *
     * @param matchingTasks Tasks matching the search keyword.
     */
    public void showMatchingTasks(List<TaskMatch> matchingTasks) {
        renderer.showMatchingTasks(matchingTasks);
    }

    /**
     * Displays tasks occurring on a date.
     *
     * @param tasks Application task list.
     * @param date  Date to match.
     */
    public void showTasksOnDate(List<Task> tasks, LocalDate date) {
        renderer.showTasksOnDate(tasks, date);
    }

    /**
     * Displays a successful task addition.
     *
     * @param task      Added task.
     * @param taskCount Number of tasks after the addition.
     */
    public void showAddedTask(Task task, int taskCount) {
        renderer.showAddedTask(task, taskCount);
    }

    /**
     * Displays a successful mark or unmark operation.
     *
     * @param task        Updated task.
     * @param commandType Mark or unmark command type.
     */
    public void showUpdatedTask(Task task, CommandType commandType) {
        renderer.showUpdatedTask(task, commandType);
    }

    /**
     * Displays a successful replacement of a duplicate task.
     *
     * @param task Replacement task.
     */
    public void showEditedTask(Task task) {
        renderer.showEditedTask(task);
    }

    /**
     * Displays a successful deletion.
     *
     * @param task      Deleted task.
     * @param taskCount Number of tasks after deletion.
     */
    public void showDeletedTask(Task task, int taskCount) {
        renderer.showDeletedTask(task, taskCount);
    }

    /**
     * Displays a successful undo operation.
     *
     * @param description Description of the reversed command.
     */
    public void showUndo(String description) {
        renderer.showUndo(description);
    }

    /**
     * Displays that there are no commands available to undo.
     */
    public void showUndoUnavailable() {
        renderer.showUndoUnavailable();
    }

    /**
     * Displays a loading failure.
     */
    public void showLoadingError() {
        renderer.showLoadingError();
    }

    /**
     * Displays a saving failure.
     */
    public void showSavingError() {
        renderer.showSavingError();
    }

    /**
     * Displays the tasks that caused a duplicate-task conflict.
     *
     * @param conflict Duplicate task details.
     */
    public void showDuplicateConflict(DuplicateTaskConflict conflict) {
        renderer.showDuplicateConflict(conflict);
    }

    /**
     * Displays the duplicate-resolution choices.
     */
    public void showDuplicatePrompt() {
        renderer.showDuplicatePrompt();
    }

    /**
     * Displays the prompt used to choose among multiple duplicate tasks.
     */
    public void showDuplicateSelection() {
        renderer.showDuplicateSelection();
    }

    /**
     * Displays feedback for an invalid duplicate-resolution choice.
     */
    public void showInvalidDuplicateChoice() {
        renderer.showInvalidDuplicateChoice();
    }

    /**
     * Displays feedback for an invalid duplicate-task selection.
     */
    public void showInvalidDuplicateSelection() {
        renderer.showInvalidDuplicateSelection();
    }

    /**
     * Displays a structured command error.
     *
     * @param exception Error to display.
     */
    public void showError(UsageException exception) {
        renderer.showError(exception);
    }
}
