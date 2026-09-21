package yqr.command;

import yqr.exception.YqrException;
import yqr.storage.Storage;
import yqr.task.Task;
import yqr.task.TaskList;
import yqr.ui.Ui;

/**
 * Deletes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that deletes the task with the given one-based number.
     *
     * @param taskNumber one-based number of the task to delete.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes the task, displays confirmation, and saves the updated list.
     *
     * @param tasks task list from which the task is deleted.
     * @param ui user interface used to display confirmation.
     * @param storage storage used to save the updated list.
     * @throws YqrException if the task number is invalid or the list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws YqrException {
        Task deletedTask = tasks.deleteTask(taskNumber);
        try {
            storage.saveTasks(tasks);
        } catch (YqrException e) {
            tasks.rollbackLastChange();
            throw e;
        }
        tasks.confirmLastChange();
        ui.showTaskDeleted(deletedTask, tasks.getTaskCount());
    }
}
