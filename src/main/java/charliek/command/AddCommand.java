package charliek.command;

import charliek.exception.DuplicateTaskException;
import charliek.exception.TaskStorageException;
import charliek.model.Task;
import charliek.model.TaskList;
import charliek.storage.Storage;
import charliek.ui.Ui;

/**
 * Adds a parsed task to the task list and persists the updated list.
 */
public class AddCommand extends Command {
    /**
     * The task to add.
     */
    private final Task task;

    /**
     * The task list to update.
     */
    private final TaskList tasks;

    /**
     * The UI used to show the result.
     */
    private final Ui ui;

    /**
     * The storage used to persist the updated list.
     */
    private final Storage storage;

    /**
     * Creates an add command for a parsed task.
     *
     * @param tasks the task list to update.
     * @param ui the UI used to show the result.
     * @param storage the storage used to persist the updated list.
     * @param task the task to add.
     */
    public AddCommand(TaskList tasks, Ui ui, Storage storage, Task task) {
        this.tasks = tasks;
        this.ui = ui;
        this.storage = storage;
        this.task = task;
    }

    /**
     * Adds the task, saves the updated list, and reports the successful addition.
     *
     * @throws TaskStorageException if the updated task list cannot be saved.
     */
    @Override
    public void execute() throws TaskStorageException, DuplicateTaskException {
        if (tasks.containsEquivalent(task)) {
            throw new DuplicateTaskException();
        }
        tasks.add(task);
        saveTasksOrRollback(storage, tasks, () -> tasks.remove(tasks.size() - 1));
        ui.showTaskAdded(task, tasks.size());
    }
}
