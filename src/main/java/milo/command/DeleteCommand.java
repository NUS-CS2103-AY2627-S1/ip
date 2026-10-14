package milo.command;

import milo.exception.MiloException;
import milo.storage.Storage;
import milo.task.Task;
import milo.task.TaskList;
import milo.ui.Ui;

/**
 * Deletes a task selected by its list number.
 */
public class DeleteCommand extends TaskCommand {

    /**
     * Creates a command that deletes the task at the given one-based number.
     *
     * @param taskNumber one-based number of the task to delete.
     */
    public DeleteCommand(int taskNumber) {
        super(taskNumber);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MiloException {
        int taskIndex = getTaskIndex(tasks);
        Task deletedTask = tasks.delete(taskIndex);
        try {
            storage.save(tasks);
        } catch (MiloException exception) {
            tasks.add(taskIndex, deletedTask);
            throw exception;
        }
        ui.showTaskDeleted(deletedTask, tasks.size());
    }
}
