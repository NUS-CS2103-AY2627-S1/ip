package kaykay.command;

import java.io.IOException;

import kaykay.exception.KaykayException;
import kaykay.model.ApplicationData;
import kaykay.model.Task;
import kaykay.model.TaskList;
import kaykay.storage.Storage;
import kaykay.ui.Ui;

/**
 * Provides shared persistence and rollback behavior for mark commands.
 */
public abstract class ChangeStatusCommand extends Command {
    /** User-supplied one-based task number. */
    private final String taskNumber;

    /** Whether the command marks the task done rather than not done. */
    private final boolean shouldMark;

    /**
     * Creates a status-change command.
     *
     * @param taskNumber one-based number of the task to change.
     * @param shouldMark whether the task should be marked as done.
     */
    protected ChangeStatusCommand(String taskNumber, boolean shouldMark) {
        this.taskNumber = taskNumber;
        this.shouldMark = shouldMark;
    }

    /**
     * Changes the selected task's completion status, saves the result, and confirms it.
     * The previous status is restored if saving fails.
     *
     * @param data application data containing the task to change.
     * @param ui UI used to show the confirmation.
     * @param storage storage used to persist the updated task list.
     * @throws KaykayException if the task number is invalid.
     * @throws IOException if the updated task list cannot be saved.
     */
    @Override
    public final void execute(ApplicationData data, Ui ui, Storage storage)
            throws KaykayException, IOException {
        TaskList tasks = data.getTasks();
        if (!tasks.isValidTaskNumber(taskNumber)) {
            throw new KaykayException("Please provide an existing task number to mark or unmark.");
        }
        int index = Integer.parseInt(taskNumber) - 1;
        assert index >= 0 && index < tasks.size()
                : "A validated task number must map to an existing task index";

        Task changedTask = tasks.getTask(index);
        boolean wasDone = changedTask.isDone();
        if (wasDone == shouldMark) {
            ui.showUnchangedTaskStatus(changedTask, shouldMark);
            return;
        }
        if (shouldMark) {
            changedTask.mark();
        } else {
            changedTask.unmark();
        }
        assert changedTask.getStatusIcon().equals(shouldMark ? "X" : " ")
                : "Changing a task status must produce the requested state";
        try {
            storage.saveData(data);
        } catch (IOException exception) {
            if (wasDone) {
                changedTask.mark();
            } else {
                changedTask.unmark();
            }
            assert changedTask.getStatusIcon().equals(wasDone ? "X" : " ")
                    : "A failed save must restore the original task status";
            throw exception;
        }
        ui.showMarkedTask(changedTask, shouldMark);
    }
}
