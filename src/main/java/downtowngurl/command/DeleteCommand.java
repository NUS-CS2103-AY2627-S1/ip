package downtowngurl.command;

import downtowngurl.exception.DowntownGurlException;
import downtowngurl.storage.Storage;
import downtowngurl.task.Task;
import downtowngurl.task.TaskList;
import downtowngurl.ui.Ui;

/**
 * Represents the command that deletes a task.
 */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command for deleting the selected task.
     *
     * @param taskIndex Zero-based index of the task to delete.
     */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Deletes the selected task and saves the updated task list.
     *
     * @param tasks Current task list.
     * @param storage Storage helper used to save task changes.
     * @param ui UI helper used to show command results.
     * @throws DowntownGurlException If the task index is invalid or the change cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Storage storage, Ui ui) throws DowntownGurlException {
        requireValidTaskIndex(tasks, this.taskIndex);
        int originalSize = tasks.size();
        Task removedTask = tasks.remove(this.taskIndex);
        assert tasks.size() == originalSize - 1 : "Deleting a task should decrease the task count by one.";
        try {
            storage.saveTasks(tasks);
        } catch (DowntownGurlException e) {
            tasks.add(this.taskIndex, removedTask);
            assert tasks.size() == originalSize : "Failed delete should restore the task count.";
            assert tasks.get(this.taskIndex) == removedTask : "Failed delete should restore the removed task.";
            throw e;
        }
        ui.showDeletedTask(removedTask, tasks.size());
    }
}
