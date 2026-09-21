package yqr.command;

import yqr.exception.YqrException;
import yqr.storage.Storage;
import yqr.task.Task;
import yqr.task.TaskList;
import yqr.ui.Ui;

/**
 * Adds a task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, displays confirmation, and saves the updated list.
     *
     * @param tasks task list to which the task is added.
     * @param ui user interface used to display confirmation.
     * @param storage storage used to save the updated list.
     * @throws YqrException if the updated list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws YqrException {
        tasks.addTask(task);
        try {
            storage.saveTasks(tasks);
        } catch (YqrException e) {
            tasks.rollbackLastChange();
            throw e;
        }
        tasks.confirmLastChange();
        ui.showTaskAdded(task, tasks.getTaskCount());
    }
}
