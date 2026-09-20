package grower.commands;

import grower.exceptions.InvalidTaskNumberException;
import grower.tasks.Task;
import grower.tasks.TaskList;
import grower.ui.Ui;

/**
 * Represents a command that marks a task as not completed.
 */
public class UnmarkCommand extends Command {
    private final int index;

    /**
     * Creates a command that unmarks the task at the specified index.
     *
     * @param index Zero-based index of the task to unmark.
     */
    public UnmarkCommand(int index) {
        this.index = index;
    }

    @Override
    public boolean execute(TaskList tasks, Ui ui) throws InvalidTaskNumberException {
        Task unmarkedTask = tasks.unmarkTask(index);
        ui.showTaskUnmarked(unmarkedTask);
        return true;
    }
}
