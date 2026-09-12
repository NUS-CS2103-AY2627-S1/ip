package bob.command;

import java.io.IOException;

import bob.ResponseType;
import bob.exception.BobException;
import bob.storage.Storage;
import bob.task.Task;
import bob.task.TaskList;

/**
 * Command that adds a task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the specified task.
     *
     * @param task task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, displays confirmation, and persists the updated list.
     *
     * @param tasks task list to update.
     * @param storage storage used to persist the updated list.
     * @return response confirming that the task was added.
     * @throws IOException if the updated task list cannot be saved.
     */
    @Override
    public String execute(TaskList tasks, Storage storage) throws BobException, IOException {
        if (tasks.containsEquivalentTask(task)) {
            throw new BobException("That task is already in your list.");
        }
        tasks.add(task);
        saveTasks(tasks, storage);

        return addResponse(task, tasks.getTaskCount());
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.ADD;
    }
}
