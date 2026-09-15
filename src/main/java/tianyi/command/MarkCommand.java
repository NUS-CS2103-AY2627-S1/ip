package tianyi.command;

import tianyi.Response;
import tianyi.TianyiException;
import tianyi.storage.Storage;
import tianyi.task.Task;
import tianyi.task.TaskList;

/**
 * Marks a task as completed.
 */
public class MarkCommand extends Command {
    private final int index;

    /**
     * Creates a command that marks a task at a zero-based index as completed.
     *
     * @param index Zero-based index of the task to mark.
     */
    public MarkCommand(int index) {
        this.index = index;
    }

    /**
     * Marks the task, saves the updated list, and returns confirmation.
     *
     * @param tasks Task list to update.
     * @param storage Storage used to persist the updated list.
     * @return Confirmation describing the marked task.
     * @throws TianyiException If the updated task list cannot be saved.
     */
    @Override
    public Response execute(TaskList tasks, Storage storage)
            throws TianyiException {
        int incompleteBefore = tasks.countIncompleteTasks();
        Task updatedTask = tasks.markTask(index);

        storage.save(tasks.getTasks());

        String footer = "";

        if (incompleteBefore > 0 && tasks.countIncompleteTasks() == 0) {
            footer = "That's everything done. You've earned a little rest!";
        }

        return new Response(
                "Well done! I've marked this task as complete:",
                "  " + updatedTask,
                footer
        );
    }
}
