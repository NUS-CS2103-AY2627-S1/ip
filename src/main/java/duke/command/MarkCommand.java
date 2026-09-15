package duke.command;

import duke.exception.GaryException;
import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Marks or unmarks a task as done.
 *
 * <p>The provided argument is expected to be a 1-based task number.</p>
 */
public class MarkCommand extends Command {
    /** Raw task number argument provided by the user. */
    private final String argument;
    /** Whether to mark as done ({@code true}) or undone ({@code false}). */
    private final boolean isDone;

    /**
     * Creates a mark/unmark command.
     *
     * @param argument Raw 1-based task number string.
     * @param isDone {@code true} to mark as done, {@code false} to mark as not done.
     */
    public MarkCommand(String argument, boolean isDone) {
        this.argument = argument;
        this.isDone = isDone;
    }

    /**
     * Updates the completion status of the specified task.
     *
     * @param tasks Task list to update.
     * @param ui UI used to show the result.
     * @throws GaryException If the argument is blank, not a number, or out of range.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        if (argument.isBlank()) {
            throw new GaryException("Please indicate which task number to update!");
        }

        String cleanedArgument = argument.trim();
        if (!cleanedArgument.matches("[1-9]\\d*")) {
            throw new GaryException("Please provide a positive whole-number task number.");
        }

        int task;
        try {
            task = Integer.parseInt(cleanedArgument) - 1;
        } catch (NumberFormatException e) {
            throw new GaryException("That task number is too large.");
        }

        tasks.validateIndex(task);
        tasks.mark(task, isDone);
        ui.showMarkedTask(task + 1, isDone);
    }
}
