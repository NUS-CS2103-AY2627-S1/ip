package yqr.command;

import yqr.exception.YqrException;
import yqr.storage.Storage;
import yqr.task.Task;
import yqr.task.TaskList;
import yqr.ui.Ui;

/**
 * Marks a task as done.
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that marks a task as done.
     *
     * @param taskNumber one-based number of the task to mark.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the task, displays confirmation, and saves the updated list.
     *
     * @param tasks task list containing the task.
     * @param ui user interface used to display confirmation.
     * @param storage storage used to save the updated list.
     * @throws YqrException if the task number is invalid or the list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws YqrException {
        Task task = tasks.markTaskAsDone(taskNumber);
        try {
            storage.saveTasks(tasks);
        } catch (YqrException e) {
            tasks.rollbackLastChange();
            throw e;
        }
        tasks.confirmLastChange();
        ui.showTaskStatusChange(task, true);
    }
}
