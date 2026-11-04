package soar.command;

import soar.exception.SoarException;
import soar.parser.CommandType;
import soar.storage.Storage;
import soar.task.Task;
import soar.task.TaskList;
import soar.ui.Ui;

/**
 * Marks one selected task as incomplete and persists the change.
 */
public class UnmarkCommand extends Command {
    /** One-based task number entered by the user. */
    private final int taskNumber;

    /**
     * Creates a command for a parsed task number.
     *
     * @param taskNumber One-based number of the task to unmark.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the selected task as incomplete, persists the change, and reports it.
     *
     * @param tasks task list to update
     * @param ui interface used to show the confirmation
     * @param storage storage used to persist the updated list
     * @throws SoarException if the task number is invalid or the change cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws SoarException {
        int taskIndex = requireTaskIndex(taskNumber, tasks, CommandType.UNMARK);
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        tasks.unmark(taskIndex);
        assert !task.isDone() : "An unmarked task should be incomplete";
        saveChange(storage, tasks, () -> {
            tasks.restoreCompletion(taskIndex, wasDone);
            assert task.isDone() == wasDone
                    : "Unmark rollback should restore the original completion state";
        });
        ui.showTaskMarked(task, false);
    }
}
