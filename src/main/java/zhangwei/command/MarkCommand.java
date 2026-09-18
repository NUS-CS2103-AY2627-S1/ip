package zhangwei.command;

import zhangwei.task.Task;
import zhangwei.task.TaskList;
import zhangwei.ui.Ui;

/** Marks the task with the given number as done. */
public class MarkCommand extends TaskNumberCommand {

    /**
     * Creates a command that will mark the task with the given number as done.
     *
     * @param taskNumber the 1-based number of the task to mark, as the user
     *     sees it in the list.
     */
    public MarkCommand(int taskNumber) {
        super(taskNumber);
    }

    /**
     * Marks the numbered task as done and shows it to the user.
     *
     * @param taskNumber the 1-based number of the task to mark.
     * @param tasks the task list holding it.
     * @param ui used to show the newly marked task.
     */
    @Override
    protected void actOnTask(int taskNumber, TaskList tasks, Ui ui) {
        Task task = tasks.get(taskNumber);
        task.markAsDone();
        ui.showTaskMarked(task);
    }
}
