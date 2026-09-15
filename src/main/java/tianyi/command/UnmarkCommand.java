package tianyi.command;

import tianyi.Response;
import tianyi.TianyiException;
import tianyi.storage.Storage;
import tianyi.task.Task;
import tianyi.task.TaskList;

/**
 * Marks a task as not completed.
 */
public class UnmarkCommand extends Command {
    private final int index;

    /**
     * Creates a command that marks a task at a zero-based index as incomplete.
     *
     * @param index Zero-based index of the task to unmark.
     */
    public UnmarkCommand(int index) {
        this.index = index;
    }

    /**
     * Unmarks the task, saves the updated list, and returns confirmation.
     *
     * @param tasks Task list to update.
     * @param storage Storage used to persist the updated list.
     * @return Confirmation describing the unmarked task.
     * @throws TianyiException If the updated task list cannot be saved.
     */
    @Override
    public Response execute(TaskList tasks, Storage storage)
            throws TianyiException {
        Task updatedTask = tasks.unmarkTask(index);

        storage.save(tasks.getTasks());

        return new Response(
                "Of course. I've marked this task as not done yet:",
                "  " + updatedTask
        );
    }
}
