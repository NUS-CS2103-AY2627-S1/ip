package yqr.command;

import yqr.exception.YqrException;
import yqr.storage.Storage;
import yqr.task.Task;
import yqr.task.TaskList;
import yqr.ui.Ui;

/**
 * Marks a task as not done.
 */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that marks a task as not done.
     *
     * @param taskNumber one-based number of the task to unmark.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Unmarks the task, displays confirmation, and saves the updated list.
     *
     * @param tasks task list containing the task.
     * @param ui user interface used to display confirmation.
     * @param storage storage used to save the updated list.
     * @throws YqrException if the task number is invalid or the list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws YqrException {
        Task task = tasks.markTaskAsNotDone(taskNumber);
        try {
            storage.saveTasks(tasks);
        } catch (YqrException e) {
            tasks.rollbackLastChange();
            throw e;
        }
        tasks.confirmLastChange();
        ui.showTaskStatusChange(task, false);
    }
}
