package duck.command;

import duck.DuckException;
import duck.storage.Storage;
import duck.task.Task;
import duck.task.TaskList;
import duck.ui.Ui;

/**
 * Deletes one task identified by its one-based task number.
 */
public class DeleteCommand extends Command {
    /** One-based task number supplied by the user. */
    private final int taskNumber;

    /**
     * Creates a command that deletes the selected task.
     *
     * @param taskNumber One-based task number.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes and saves the task, restoring it to its original position if saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DuckException {
        if (this.taskNumber < 1 || this.taskNumber > tasks.getTaskCount()) {
            throw new DuckException("That task number does not exist. "
                    + "Use list to check the available task numbers.");
        }

        int taskIndex = this.taskNumber - 1;
        Task removedTask = tasks.delete(taskIndex);
        try {
            storage.save(tasks.getTasksSnapshot());
        } catch (DuckException e) {
            tasks.add(taskIndex, removedTask);
            throw e;
        }
        ui.showTaskDeleted(removedTask, tasks.getTaskCount());
    }
}
