package bob.command;

import java.io.IOException;

import bob.ResponseType;
import bob.exception.BobException;
import bob.storage.Storage;
import bob.task.Task;
import bob.task.TaskList;

/**
 * Command that marks a task as not done.
 */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command for the specified one-based task number.
     *
     * @param taskNumber one-based number of the task to unmark.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the selected task as not done and persists the updated list.
     *
     * @param taskList task list to update.
     * @param storage storage used to persist the updated list.
     * @return response confirming that the task was marked as not done.
     * @throws BobException if the task number does not identify a task.
     * @throws IOException if the updated task list cannot be saved.
     */
    @Override
    public String execute(TaskList taskList, Storage storage) throws BobException, IOException {
        Task task = getTask(taskList, taskNumber);
        task.markAsNotDone();
        saveTasks(taskList, storage);

        return unmarkResponse(task);
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.UNMARK;
    }
}
