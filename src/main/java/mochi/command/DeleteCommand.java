package mochi.command;

import mochi.MochiException;
import mochi.TaskList;
import mochi.task.Task;
import mochi.ui.Ui;

/**
 * Deletes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Creates a delete command for the task at the given 0-based index.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui) throws MochiException {
        if (index < 0 || index >= tasks.getTasks().size()) {
            throw new MochiException("There is no task number " + (index + 1) + " in the list.");
        }
        Task removed = tasks.remove(index);
        ui.showDeleted(removed, tasks.getTasks().size());
    }
}
