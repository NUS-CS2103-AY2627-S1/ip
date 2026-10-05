package kaykay.command;

import java.io.IOException;

import kaykay.exception.KaykayException;
import kaykay.model.ApplicationData;
import kaykay.model.Task;
import kaykay.model.TaskList;
import kaykay.storage.Storage;
import kaykay.ui.Ui;

/**
 * Deletes a task and persists the resulting task list.
 */
public final class DeleteCommand extends Command {
    /** User-supplied one-based task number. */
    private final String taskNumber;

    /**
     * Creates a delete command for a task number.
     *
     * @param taskNumber one-based number of the task to delete.
     */
    public DeleteCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes the selected task, saves the result, and confirms the deletion.
     * The task is restored if saving fails.
     *
     * @param data application data containing the task list to modify.
     * @param ui UI used to show the confirmation.
     * @param storage storage used to persist the updated task list.
     * @throws KaykayException if the task number is invalid.
     * @throws IOException if the updated task list cannot be saved.
     */
    @Override
    public void execute(ApplicationData data, Ui ui, Storage storage) throws KaykayException, IOException {
        TaskList tasks = data.getTasks();
        if (!tasks.isValidTaskNumber(taskNumber)) {
            throw new KaykayException("Please provide an existing task number to delete.");
        }
        int index = Integer.parseInt(taskNumber) - 1;
        assert index >= 0 && index < tasks.size()
                : "A validated task number must map to an existing task index";

        int originalTaskCount = tasks.size();
        Task deletedTask = tasks.remove(index);
        assert tasks.size() == originalTaskCount - 1 : "Deleting a task must reduce the task count by one";
        try {
            storage.saveData(data);
        } catch (IOException exception) {
            tasks.add(index, deletedTask);
            assert tasks.size() == originalTaskCount : "A failed save must restore the original task count";
            throw exception;
        }
        ui.showDeletedTask(deletedTask, tasks.size());
    }
}
