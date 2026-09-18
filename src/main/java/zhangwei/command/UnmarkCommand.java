package zhangwei.command;

import zhangwei.task.Task;
import zhangwei.task.TaskList;
import zhangwei.ui.Ui;

/** Marks the task with the given number as not done. */
public class UnmarkCommand extends TaskNumberCommand {

    /**
     * Creates a command that will mark the task with the given number as not
     * done.
     *
     * @param taskNumber the 1-based number of the task to unmark, as the user
     *     sees it in the list.
     */
    public UnmarkCommand(int taskNumber) {
        super(taskNumber);
    }

    /**
     * Marks the numbered task as not done and shows it to the user.
     *
     * @param taskNumber the 1-based number of the task to unmark.
     * @param tasks the task list holding it.
     * @param ui used to show the newly unmarked task.
     */
    @Override
    protected void actOnTask(int taskNumber, TaskList tasks, Ui ui) {
        Task task = tasks.get(taskNumber);
        task.markAsNotDone();
        ui.showTaskUnmarked(task);
    }
}
