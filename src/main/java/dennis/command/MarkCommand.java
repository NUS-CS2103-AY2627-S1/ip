package dennis.command;

import dennis.DennisException;
import dennis.storage.Storage;
import dennis.task.Task;
import dennis.task.TaskList;
import dennis.ui.Ui;

/**
 * Marks one task as done and saves the change.
 */
public class MarkCommand extends TaskCommand {
    /**
     * Creates a mark command for the task at the given list position.
     *
     * @param taskNumber position of the task to mark (1 = first task)
     */
    public MarkCommand(int taskNumber) {
        super(taskNumber);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws DennisException {
        checkInRange(tasks);

        // checkInRange has just guaranteed 1 <= taskNumber <= size, so
        // taskNumber - 1 is a valid 0-based index for the list access below.
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "checkInRange should have rejected taskNumber " + taskNumber;

        Task task = tasks.get(taskNumber - 1);
        task.markAsDone();
        storage.save(tasks.asList());

        ui.showMarkedTask(task);
    }
}
