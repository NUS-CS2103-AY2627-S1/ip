package tianyi.command;

import tianyi.Response;
import tianyi.TianyiException;
import tianyi.storage.Storage;
import tianyi.task.Task;
import tianyi.task.TaskList;

/**
 * Adds a task to the task list.
 */
public class AddCommand extends Command {
    private static final int INCOMPLETE_TASK_LIMIT = 5;

    private final Task task;

    /**
     * Creates a command that adds the specified task.
     *
     * @param task Task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, saves the updated list, and returns confirmation.
     *
     * @param tasks Task list to update.
     * @param storage Storage used to persist the updated list.
     * @return Confirmation describing the added task.
     * @throws TianyiException If the updated task list cannot be saved.
     */
    @Override
    public Response execute(TaskList tasks, Storage storage)
            throws TianyiException {
        tasks.addTask(task);
        storage.save(tasks.getTasks());

        String encouragement = tasks.countIncompleteTasks() <= INCOMPLETE_TASK_LIMIT
                ? "You've got this. I'm cheering for you!"
                : "Remember to rest, too. Take care of yourself.";

        return new Response(
                "Got it. I've added this task for you:",
                "  " + task,
                "Now you have " + tasks.size() + " tasks in the list.\n"
                        + encouragement
        );
    }
}
