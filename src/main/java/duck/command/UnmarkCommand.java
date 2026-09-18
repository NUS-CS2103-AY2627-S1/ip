package duck.command;

import duck.task.Task;
import duck.ui.Ui;

/**
 * Marks one task as not completed.
 */
public class UnmarkCommand extends TaskStatusCommand {
    /**
     * Creates a command that marks the selected task as undone.
     *
     * @param taskNumber One-based task number.
     */
    public UnmarkCommand(int taskNumber) {
        super(taskNumber, false);
    }

    /** Shows confirmation that the selected task is now not done. */
    @Override
    protected void showConfirmation(Ui ui, Task task) {
        ui.showTaskUnmarked(task);
    }
}
