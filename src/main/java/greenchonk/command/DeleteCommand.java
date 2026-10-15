package greenchonk.command;

import greenchonk.exception.GreenChonkException;
import greenchonk.storage.Storage;
import greenchonk.task.Task;
import greenchonk.task.TaskList;
import greenchonk.ui.Ui;

/**
 * Deletes a task from the task list.
 */
public class DeleteCommand extends Command {
    private static final String COMMAND_NAME = "delete";

    private final int taskNumber;

    /**
     * Creates a command that deletes the specified task number.
     *
     * @param taskNumber the one-based task number to delete.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes the configured task and persists the updated task list.
     * Restores the deleted task at its original position if persistence fails.
     *
     * @param tasks the task list to update
     * @param ui the UI that reports the deletion
     * @param storage the storage to which the updated list is saved
     * @throws GreenChonkException if the task number is invalid or saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws GreenChonkException {
        int taskIndex = getTaskIndex(taskNumber, COMMAND_NAME, tasks);
        Task deletedTask = tasks.delete(taskIndex);
        try {
            storage.save(tasks);
        } catch (GreenChonkException exception) {
            tasks.add(taskIndex, deletedTask);
            throw exception;
        }
        ui.showTaskDeleted(deletedTask, tasks.size());
    }
}
