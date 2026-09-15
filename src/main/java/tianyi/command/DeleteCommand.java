package tianyi.command;

import tianyi.Response;
import tianyi.TianyiException;
import tianyi.storage.Storage;
import tianyi.task.Task;
import tianyi.task.TaskList;

/**
 * Deletes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Creates a command that deletes a task at a zero-based index.
     *
     * @param index Zero-based index of the task to delete.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    /**
     * Deletes the task, saves the updated list, and returns confirmation.
     *
     * @param tasks Task list to update.
     * @param storage Storage used to persist the updated list.
     * @return Confirmation describing the deleted task.
     * @throws TianyiException If the updated task list cannot be saved.
     */
    @Override
    public Response execute(TaskList tasks, Storage storage)
            throws TianyiException {
        Task updatedTask = tasks.deleteTask(index);

        storage.save(tasks.getTasks());

        return new Response(
                "All right. I've removed this task:",
                "  " + updatedTask,
                "Now you have " + tasks.size() + " tasks in the list."
        );
    }
}
