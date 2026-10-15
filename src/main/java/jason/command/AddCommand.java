package jason.command;

import jason.storage.Storage;
import jason.task.Task;
import jason.task.TaskList;
import jason.ui.Ui;

/**
 * Adds a task to the task list and persists the updated list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates an add command for a task.
     *
     * @param task task to add when the command executes.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, saves the list, and reports the addition.
     *
     * @param tasks task list to update.
     * @param ui user interface used to report the result.
     * @param storage storage used to persist the updated list.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.add(task);
        storage.save(tasks);
        ui.showAddedTask(task);
    }
}
