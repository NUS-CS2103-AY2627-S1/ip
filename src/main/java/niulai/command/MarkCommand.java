package niulai.command;

import niulai.model.Task;
import niulai.service.Ui;

/**
 * Marks a task as completed and persists the updated task list.
 */
public class MarkCommand extends StatusCommand {
    /**
     * Creates a mark command for a task-list index.
     *
     * @param taskIndex the zero-based index of the task to mark
     */
    public MarkCommand(int taskIndex) {
        super(Type.MARK, taskIndex);
    }

    /** Marks the task as completed. */
    @Override
    protected void applyStatus(Task task) {
        task.markAsDone();
    }

    /** Shows the successful completion message. */
    @Override
    protected void showConfirmation(Ui ui, Task task) {
        ui.showTaskMarked(task);
    }
}
