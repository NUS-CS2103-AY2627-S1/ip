package grower.commands;

import grower.exceptions.InvalidTaskNumberException;
import grower.tasks.Task;
import grower.tasks.TaskList;
import grower.ui.Ui;

/**
 * Represents a command that deletes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Creates a command that deletes the task at the specified index.
     *
     * @param index Zero-based index of the task to delete.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public boolean execute(TaskList tasks, Ui ui) throws InvalidTaskNumberException {
        Task deletedTask = tasks.deleteTask(index);
        ui.showTaskDeleted(deletedTask);
        return true;
    }
}
