package dennis.command;

import dennis.DennisException;
import dennis.storage.Storage;
import dennis.task.Task;
import dennis.task.TaskList;
import dennis.ui.Ui;

/**
 * Removes one task from the list and saves the change.
 */
public class DeleteCommand extends TaskCommand {
    /**
     * Creates a delete command for the task at the given list position.
     *
     * @param taskNumber position of the task to delete (1 = first task)
     */
    public DeleteCommand(int taskNumber) {
        super(taskNumber);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws DennisException {
        checkInRange(tasks);

        // checkInRange has just guaranteed 1 <= taskNumber <= size, so
        // taskNumber - 1 is a valid 0-based index for the removal below.
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "checkInRange should have rejected taskNumber " + taskNumber;

        Task task = tasks.remove(taskNumber - 1);
        storage.save(tasks.asList());

        ui.showRemovedTask(task, tasks.size());
    }
}
