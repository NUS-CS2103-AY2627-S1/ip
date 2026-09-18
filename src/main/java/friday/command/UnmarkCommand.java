package friday.command;

import friday.exception.FridayException;
import friday.model.Task;
import friday.model.TaskList;
import friday.storage.Storage;
import friday.ui.Ui;

/**
 * Marks one task as not completed.
 */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates an unmark command.
     *
     * @param taskNumber One-based number of the task to unmark.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Returns the one-based task number selected by this command.
     *
     * @return The selected task number.
     */
    public int getTaskNumber() {
        return taskNumber;
    }

    /**
     * Marks the requested task as incomplete and reports the change.
     *
     * @param tasks Current task list to modify.
     * @param ui UI used to display the confirmation.
     * @param storage Storage available to the command; saving is coordinated by Friday.
     * @throws FridayException If the task number is not valid.
     */
    @Override
    public void execute(TaskList tasks, TaskList archivedTasks, Ui ui, Storage storage,
            Storage archiveStorage) throws FridayException {
        Task task = getTask(tasks, taskNumber);
        task.unmarkAsDone();
        ui.showTaskStatus(task, false);
    }

    /**
     * Indicates that executing this command changes persisted state.
     *
     * @return true because a completion status is changed.
     */
    @Override
    public boolean changesTasks() {
        return true;
    }
}
