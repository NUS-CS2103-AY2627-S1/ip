package daddy.command;

import daddy.exception.DaddyException;
import daddy.storage.Storage;
import daddy.task.Task;
import daddy.task.TaskList;
import daddy.ui.Ui;

/**
 * Removes one task from the list.
 */
public class DeleteCommand extends TaskIndexCommand {
    /**
     * Creates a command for the supplied zero-based task index.
     *
     * @param taskIndex the task index to remove
     */
    public DeleteCommand(int taskIndex) {
        super(taskIndex);
    }

    /** Removes, saves, and displays the selected task. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DaddyException {
        ensureTaskExists(tasks);
        Task removedTask = tasks.removeTaskAt(getTaskIndex());
        try {
            storage.save(tasks);
        } catch (DaddyException exception) {
            tasks.insert(getTaskIndex(), removedTask);
            throw exception;
        }
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
