package pip.command;

import pip.storage.Storage;
import pip.task.Task;
import pip.task.TaskList;
import pip.ui.Ui;


/**
 * Represents a command that adds a task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates an AddCommand that will add the given task when executed.
     *
     * @param task The task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task to the task list, shows a confirmation to the user,
     * and persists the updated task list to storage.
     *
     * @param tasks The task list to add the task to.
     * @param ui The UI used to confirm the addition.
     * @param storage The storage used to persist the updated task list.
     * @throws Exception If an I/O error occurs while saving.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws Exception {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        storage.save(tasks);
    }
}
