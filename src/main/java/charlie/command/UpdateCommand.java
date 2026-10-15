package charlie.command;

import charlie.storage.Storage;
import charlie.task.Task;
import charlie.task.TaskList;
import charlie.ui.Ui;

/**
 * Replaces one task with a copy containing an updated field.
 */
public class UpdateCommand extends Command {
    /** Zero-based index of the task to update. */
    private final int index;

    /** Field to change. */
    private final UpdateField updateField;

    /** Replacement value for the selected field. */
    private final String newValue;

    /**
     * Creates a command that updates one field of the selected task.
     *
     * @param index Zero-based index of the task to update.
     * @param updateField Field to update.
     * @param newValue Replacement value for the field.
     */
    public UpdateCommand(int index, UpdateField updateField, String newValue) {
        this.index = index;
        this.updateField = updateField;
        this.newValue = newValue;
    }

    /**
     * Saves and applies the requested task update, then displays the replacement.
     *
     * @param tasks Task list containing the task to update.
     * @param ui User interface used to display the updated task.
     * @param storage Storage used to persist the replacement.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task originalTask = tasks.get(this.index);
        Task updatedTask = originalTask.createUpdatedTask(this.updateField, this.newValue);

        storage.save(tasks.getTasksAfterReplacing(this.index, updatedTask));
        tasks.replace(this.index, updatedTask);

        ui.showMessages(
                "All changed! Here's the updated task:",
                "  " + updatedTask);
    }
}
