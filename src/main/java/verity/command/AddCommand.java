package verity.command;

import verity.storage.Storage;
import verity.task.Task;
import verity.task.TaskList;

/**
 * Adds a task to the task list.
 */
public class AddCommand extends Command implements Undoable {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task Task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds this command's task to the task list.
     *
     * @param tasks {@inheritDoc}
     * @param storage {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String execute(TaskList tasks, Storage storage) {
        tasks.add(task);
        String response = "Obviously. Added:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.";
        return response + save(tasks, storage);
    }

    /**
     * Removes the task this command added, undoing it.
     *
     * @param tasks {@inheritDoc}
     * @param storage {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String undo(TaskList tasks, Storage storage) {
        tasks.remove(tasks.size() - 1);
        String response = "Whatever. Un-did it:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.";
        return response + save(tasks, storage);
    }
}
