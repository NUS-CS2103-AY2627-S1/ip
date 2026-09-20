package goop.command;

import java.io.IOException;

import goop.exception.GoopException;
import goop.storage.Storage;
import goop.task.Task;
import goop.task.TaskList;
import goop.ui.Ui;

/**
 * Represents a request to delete one task.
 */
public class DeleteCommand extends TaskCommand {
    /**
     * Creates a delete command for a one-based task number.
     *
     * @param taskNumber Task to delete.
     */
    public DeleteCommand(int taskNumber) {
        super(taskNumber);
    }

    /**
     * Deletes the task, persists the list, and rolls back if saving fails.
     *
     * @param tasks Task list from which the task is deleted.
     * @param ui User interface used to display the deleted task.
     * @param storage Storage used to persist the updated task list.
     * @throws GoopException If the requested task number is outside the list.
     * @throws IOException If the updated task list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws GoopException, IOException {
        int taskIndex = getTaskIndex(tasks, "delete");
        Task deletedTask = tasks.delete(taskIndex);
        try {
            storage.saveTasks(tasks);
        } catch (IOException error) {
            tasks.add(taskIndex, deletedTask);
            throw error;
        }
        ui.showDeletedTask(deletedTask, tasks.size());
    }
}
