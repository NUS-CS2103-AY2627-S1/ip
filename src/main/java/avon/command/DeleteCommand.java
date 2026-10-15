package avon.command;

import avon.exception.AvonException;
import avon.exception.StorageException;
import avon.storage.Storage;
import avon.task.Task;
import avon.task.TaskList;
import avon.ui.Ui;

/**
 * Removes one task from Avon's task list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that deletes the task with the given one-based number.
     *
     * @param taskNumber the one-based number of the task to delete.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws AvonException {
        int taskIndex = getTaskIndex(taskList, taskNumber, CommandType.DELETE.getKeyword());
        int originalTaskCount = taskList.size();
        Task removedTask = taskList.removeTask(taskIndex);
        assert taskList.size() == originalTaskCount - 1
                : "Deleting a task must decrease the task count by one";
        try {
            storage.save(taskList);
        } catch (StorageException exception) {
            taskList.add(taskIndex, removedTask);
            assert taskList.size() == originalTaskCount
                    : "A failed save must restore the original task count";
            throw exception;
        }
        ui.showDeletedTask(removedTask, taskList.size());
    }
}
