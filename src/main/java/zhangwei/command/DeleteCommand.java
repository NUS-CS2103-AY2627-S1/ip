package zhangwei.command;

import zhangwei.task.Task;
import zhangwei.task.TaskList;
import zhangwei.ui.Ui;

/** Removes the task with the given number from the list. */
public class DeleteCommand extends TaskNumberCommand {

    /**
     * Creates a command that will delete the task with the given number.
     *
     * @param taskNumber the 1-based number of the task to delete, as the user
     *     sees it in the list.
     */
    public DeleteCommand(int taskNumber) {
        super(taskNumber);
    }

    /**
     * Removes the numbered task and reports it, with the shortened list's new
     * size, to the user.
     *
     * @param taskNumber the 1-based number of the task to delete.
     * @param tasks the task list to delete from.
     * @param ui used to report the removal.
     */
    @Override
    protected void actOnTask(int taskNumber, TaskList tasks, Ui ui) {
        Task removed = tasks.delete(taskNumber);
        ui.showTaskRemoved(removed, tasks.size());
    }
}
