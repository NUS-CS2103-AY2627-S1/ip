package et.command;

import et.exception.ETException;
import et.storage.Storage;
import et.task.Task;
import et.task.TaskList;
import et.ui.Ui;

/**
 * Removes one task from ET's task list.
 */
public class DeleteCommand extends TaskIndexCommand {
    /**
     * Creates a command that removes one task.
     *
     * @param taskIndex the zero-based index of the task to remove
     */
    public DeleteCommand(int taskIndex) {
        super(taskIndex);
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ETException {
        Task removedTask = getTask(tasks);
        tasks.removeTask(taskIndex);
        saveTasks(tasks, ui, storage);
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
