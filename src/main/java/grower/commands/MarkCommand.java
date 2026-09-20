package grower.commands;

import grower.exceptions.InvalidTaskNumberException;
import grower.tasks.Task;
import grower.tasks.TaskList;
import grower.ui.Ui;

/**
 * Represents a command that marks a task as completed.
 */
public class MarkCommand extends Command {
    private final int index;

    /**
     * Creates a command that marks the task at the specified index.
     *
     * @param index Zero-based index of the task to mark.
     */
    public MarkCommand(int index) {
        this.index = index;
    }

    @Override
    public boolean execute(TaskList tasks, Ui ui) throws InvalidTaskNumberException {
        Task markedTask = tasks.markTask(index);
        ui.showTaskMarked(markedTask);
        return true;
    }
}
