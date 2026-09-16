package downtowngurl.command;

import downtowngurl.exception.DowntownGurlException;
import downtowngurl.storage.Storage;
import downtowngurl.task.Task;
import downtowngurl.task.TaskList;
import downtowngurl.ui.Ui;

/**
 * Represents the command that marks a task as not done.
 */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command for unmarking the selected task.
     *
     * @param taskIndex Zero-based index of the task to unmark.
     */
    public UnmarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Marks the selected task as not done and saves the updated task list.
     *
     * @param tasks Current task list.
     * @param storage Storage helper used to save task changes.
     * @param ui UI helper used to show command results.
     * @throws DowntownGurlException If the task index is invalid or the change cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Storage storage, Ui ui) throws DowntownGurlException {
        requireValidTaskIndex(tasks, this.taskIndex);
        Task task = tasks.get(this.taskIndex);
        boolean wasDone = task.isDone();
        tasks.markAsNotDone(this.taskIndex);
        assert !task.isDone() : "Unmarking a task should clear its done status.";
        try {
            storage.saveTasks(tasks);
        } catch (DowntownGurlException e) {
            restoreTaskStatus(task, wasDone);
            throw e;
        }
        ui.showUpdatedTask("Sure, I unmarked it!", task);
    }
}
