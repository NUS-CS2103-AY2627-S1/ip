package zen.command;

import zen.ZenException;
import zen.storage.Storage;
import zen.task.Task;
import zen.task.TaskList;
import zen.ui.Ui;

/**
 * Represents a base command that adds a newly created task to the task list.
 */
public abstract class AddCommand extends Command {
    /**
     * Adds the supplied task to the task list, saves the updated list, and displays the result.
     *
     * @param task Task to add.
     * @param tasks Task list to update.
     * @param ui Console interface used to display the result.
     * @param storage Persistence service used to save the updated list.
     * @throws ZenException If the updated list cannot be saved.
     */
    protected void addTask(Task task, TaskList tasks, Ui ui, Storage storage) throws ZenException {
        tasks.addTask(task);
        storage.save(tasks);
        ui.printAddTask(task, tasks.size());
    }
}
