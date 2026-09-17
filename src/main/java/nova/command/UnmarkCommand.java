package nova.command;

import nova.task.Task;
import nova.ui.Ui;

/**
 * Marks a task as not completed.
 */
public class UnmarkCommand extends TaskStatusCommand {
    /**
     * Creates a command that marks the selected task as not completed.
     *
     * @param taskNumber one-based number of the task to unmark.
     */
    public UnmarkCommand(int taskNumber) {
        super(taskNumber, "unmark", false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void showConfirmation(Ui ui, Task task) {
        ui.showTaskUnmarked(task);
    }
}
