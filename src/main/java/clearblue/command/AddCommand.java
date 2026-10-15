package clearblue.command;

import clearblue.ClearblueException;
import clearblue.storage.Storage;
import clearblue.task.Task;
import clearblue.task.TaskList;
import clearblue.ui.Ui;

/**
 * Adds a task to the list, reports it to the user, and saves the list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the wrapped task to {@code tasks}, reports it through
     * {@code ui}, and saves the updated list via {@code storage}.
     *
     * @param tasks task list to add to
     * @param ui user interface to report through
     * @param storage storage to persist the change through
     * @throws ClearblueException if saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ClearblueException {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        storage.save(tasks.asList());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true}
     */
    @Override
    public boolean isUndoable() {
        return true;
    }

    /**
     * Removes the task this command added. Since {@link #execute} always
     * appends to the end of the list, and undo only ever reverses the most
     * recently executed command, the added task is always still last.
     *
     * @param tasks task list to remove the added task from
     * @param ui user interface to report through
     * @param storage storage to persist the change through
     * @throws ClearblueException if saving fails
     */
    @Override
    public void undo(TaskList tasks, Ui ui, Storage storage) throws ClearblueException {
        Task removedTask = tasks.remove(tasks.size() - 1);
        ui.showTaskRemoved(removedTask, tasks.size());
        storage.save(tasks.asList());
    }
}
