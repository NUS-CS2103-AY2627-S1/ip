package soar.command;

import soar.exception.SoarException;
import soar.parser.CommandType;
import soar.storage.Storage;
import soar.task.Task;
import soar.task.TaskList;
import soar.ui.Ui;

/**
 * Deletes one selected task and persists the updated task list.
 */
public class DeleteCommand extends Command {
    /** One-based task number entered by the user. */
    private final int taskNumber;

    /**
     * Creates a deletion command for a parsed task number.
     *
     * @param taskNumber One-based number of the task to delete.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes the selected task, persists the change, and reports the deletion.
     *
     * @param tasks task list to update
     * @param ui interface used to show the confirmation
     * @param storage storage used to persist the updated list
     * @throws SoarException if the task number is invalid or the change cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws SoarException {
        int taskIndex = requireTaskIndex(taskNumber, tasks, CommandType.DELETE);
        int originalTaskCount = tasks.size();
        Task removedTask = tasks.delete(taskIndex);
        assert tasks.size() == originalTaskCount - 1
                : "Deleting a task should decrease the task count by one";
        saveChange(storage, tasks, () -> {
            tasks.restoreDeletedTask(taskIndex, removedTask);
            assert tasks.size() == originalTaskCount
                    : "Deletion rollback should restore the original task count";
            assert tasks.get(taskIndex) == removedTask
                    : "Deletion rollback should restore the task at its original index";
        });
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
