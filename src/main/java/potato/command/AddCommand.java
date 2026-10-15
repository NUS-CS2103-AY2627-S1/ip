package potato.command;

import potato.exception.PotatoException;
import potato.storage.Storage;
import potato.task.Task;
import potato.tasklist.TaskList;
import potato.ui.Ui;

/**
 * Command to add a new task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Constructs an {@code AddCommand} with the specified task to add.
     *
     * @param task Task to be added.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Executes the task addition, updates storage, and notifies the user.
     *
     * @param tasks Task list to modify.
     * @param ui User interface for displaying output notifications.
     * @param storage Persistence layer for saving updated task list.
     * @throws PotatoException If an error occurs during file saving.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PotatoException {
        tasks.add(task);
        storage.save(tasks.getTasks());
        ui.showTaskAdded(task, tasks.size());
    }
}