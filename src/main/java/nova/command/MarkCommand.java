package nova.command;

import nova.task.Task;
import nova.ui.Ui;

/**
 * Marks a task as completed.
 */
public class MarkCommand extends TaskStatusCommand {
    /**
     * Creates a command that marks the selected task as completed.
     *
     * @param taskNumber one-based number of the task to mark.
     */
    public MarkCommand(int taskNumber) {
        super(taskNumber, "mark", true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void showConfirmation(Ui ui, Task task) {
        ui.showTaskMarked(task);
    }
}
