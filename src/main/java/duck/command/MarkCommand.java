package duck.command;

import duck.task.Task;
import duck.ui.Ui;

/**
 * Marks one task as completed.
 */
public class MarkCommand extends TaskStatusCommand {
    /**
     * Creates a command that marks the selected task as done.
     *
     * @param taskNumber One-based task number.
     */
    public MarkCommand(int taskNumber) {
        super(taskNumber, true);
    }

    /** Shows confirmation that the selected task is now done. */
    @Override
    protected void showConfirmation(Ui ui, Task task) {
        ui.showTaskMarked(task);
    }
}
