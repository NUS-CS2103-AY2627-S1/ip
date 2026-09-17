package nova.command;

import nova.exception.NovaException;
import nova.storage.Storage;
import nova.task.Task;
import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Adds a task and persists the updated task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the supplied task.
     *
     * @param task parsed task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds and saves the task, rolling back the addition if saving fails.
     *
     * @param tasks task list to update.
     * @param ui user interface used to display the confirmation.
     * @param storage storage used to persist the updated list.
     * @throws NovaException if the updated list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        tasks.add(task);
        try {
            storage.save(tasks.getTasks());
        } catch (NovaException e) {
            tasks.remove(tasks.size() - 1);
            throw e;
        }
        ui.showTaskAdded(task, tasks.size());
    }
}
