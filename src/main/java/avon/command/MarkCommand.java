package avon.command;

import avon.exception.AvonException;
import avon.exception.StorageException;
import avon.storage.Storage;
import avon.task.Task;
import avon.task.TaskList;
import avon.ui.Ui;

/**
 * Marks one task as completed.
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that marks the task with the given one-based number.
     *
     * @param taskNumber the one-based number of the task to mark.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws AvonException {
        int taskIndex = getTaskIndex(taskList, taskNumber, CommandType.MARK.getKeyword());
        Task task = taskList.getTask(taskIndex);
        boolean wasDone = task.isDone();
        task.markAsDone();
        assert task.isDone() : "A marked task must be done";
        try {
            storage.save(taskList);
        } catch (StorageException exception) {
            if (!wasDone) {
                task.markAsNotDone();
            }
            assert task.isDone() == wasDone
                    : "A failed save must restore the original completion state";
            throw exception;
        }
        ui.showMarkedTask(task);
    }
}
