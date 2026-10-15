package bob.command;

import java.io.IOException;

import bob.BobException;
import bob.Storage;
import bob.Ui;
import bob.task.Task;
import bob.task.TaskList;

/**
 * Represents a request to delete one task.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command for a one-based task number.
     *
     * @param taskNumber number of the task to delete
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes and saves the selected task, restoring it if saving fails.
     *
     * @param tasks task list to update
     * @param ui user interface used for confirmation
     * @param storage storage used to persist the updated list
     * @throws BobException if the task number is outside the list
     * @throws IOException if the updated task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BobException, IOException {
        int taskIndex = tasks.getTaskIndex(taskNumber);
        Task removedTask = tasks.delete(taskIndex);
        try {
            storage.save(tasks.asList());
        } catch (IOException exception) {
            tasks.restoreDeletedTask(taskIndex, removedTask);
            throw exception;
        }
        ui.showDeleted(removedTask, tasks.size());
    }
}
