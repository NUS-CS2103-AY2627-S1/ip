package niulai.command;

import niulai.model.Task;
import niulai.service.Ui;

/**
 * Marks a task as pending and persists the updated task list.
 */
public class UnmarkCommand extends StatusCommand {
    /**
     * Creates an unmark command for a task-list index.
     *
     * @param taskIndex the zero-based index of the task to mark as pending
     */
    public UnmarkCommand(int taskIndex) {
        super(Type.UNMARK, taskIndex);
    }

    /** Marks the task as pending. */
    @Override
    protected void applyStatus(Task task) {
        task.markAsNotDone();
    }

    /** Shows the successful pending-status message. */
    @Override
    protected void showConfirmation(Ui ui, Task task) {
        ui.showTaskUnmarked(task);
    }
}
