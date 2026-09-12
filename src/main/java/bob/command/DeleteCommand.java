package bob.command;

import java.io.IOException;

import bob.ResponseType;
import bob.exception.BobException;
import bob.storage.Storage;
import bob.task.Task;
import bob.task.TaskList;

/**
 * Command that removes a task from the list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command for the specified one-based task number.
     *
     * @param taskNumber one-based number of the task to delete.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Removes the selected task and persists the updated list.
     *
     * @param taskList task list to update.
     * @param storage storage used to persist the updated list.
     * @return response confirming which task was removed.
     * @throws BobException if the task number does not identify a task.
     * @throws IOException if the updated task list cannot be saved.
     */
    @Override
    public String execute(TaskList taskList, Storage storage) throws BobException, IOException {
        Task deletedTask = taskList.remove(taskNumber - 1);
        saveTasks(taskList, storage);

        int taskCount = taskList.getTaskCount();
        return deleteResponse(deletedTask, taskCount);
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.DELETE;
    }
}
