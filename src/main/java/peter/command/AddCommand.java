package peter.command;

import peter.PeterException;
import peter.storage.Storage;
import peter.task.Task;
import peter.task.TaskList;
import peter.ui.Ui;

/**
 * Adds a new task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds an already-parsed task.
     *
     * @param task task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Adds the task, then saves. The task is only reported to the user
     * once the save succeeds, so a failed save removes it again rather than
     * confirming an addition that was never stored.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PeterException {
        tasks.add(task);
        // The rollback below deletes whichever task is last, so it only undoes
        // this command while the task just added is still the last one.
        assert tasks.get(tasks.size() - 1) == task : "the added task must be the last in the list";

        saveOrRollback(tasks, storage, () -> tasks.delete(tasks.size() - 1));
        ui.showAddedTask(task, tasks.size());
    }
}
