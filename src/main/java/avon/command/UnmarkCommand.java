package avon.command;

import avon.exception.AvonException;
import avon.exception.StorageException;
import avon.storage.Storage;
import avon.task.Task;
import avon.task.TaskList;
import avon.ui.Ui;

/**
 * Marks one completed task as not completed.
 */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that unmarks the task with the given one-based number.
     *
     * @param taskNumber the one-based number of the task to unmark.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws AvonException {
        int taskIndex = getTaskIndex(taskList, taskNumber, CommandType.UNMARK.getKeyword());
        Task task = taskList.getTask(taskIndex);
        boolean wasDone = task.isDone();
        task.markAsNotDone();
        assert !task.isDone() : "An unmarked task must not be done";
        try {
            storage.save(taskList);
        } catch (StorageException exception) {
            if (wasDone) {
                task.markAsDone();
            }
            assert task.isDone() == wasDone
                    : "A failed save must restore the original completion state";
            throw exception;
        }
        ui.showUnmarkedTask(task);
    }
}
