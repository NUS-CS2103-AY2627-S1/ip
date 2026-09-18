package habpyduck.command;

import habpyduck.HabpyDuckException;
import habpyduck.storage.Storage;
import habpyduck.task.Task;
import habpyduck.task.TaskList;
import habpyduck.ui.Ui;

/**
 * Deletes one task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that deletes the task with the given user-facing number.
     *
     * @param taskNumber the one-based task number entered by the user.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes the task, saves the updated list, and reports the deleted task.
     *
     * @param tasks the task list to update.
     * @param ui the UI used to show command results.
     * @param storage the storage used to save tasks.
     * @throws HabpyDuckException if the task does not exist or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws HabpyDuckException {
        int taskIndex = requireValidTaskIndex(taskNumber, tasks);
        Task removedTask = tasks.delete(taskIndex);
        try {
            storage.saveTasks(tasks.asList());
        } catch (HabpyDuckException e) {
            assert taskIndex >= 0 && taskIndex <= tasks.size()
                    : "Deleted task should be insertable at its original index for rollback";
            tasks.insert(taskIndex, removedTask);
            throw e;
        }
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
