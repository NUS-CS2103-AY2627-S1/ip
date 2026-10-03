package ramly.command;

import ramly.exception.TaskValidationException;
import ramly.model.Task;
import ramly.model.TaskList;
import ramly.storage.Storage;
import ramly.ui.Ui;

/** Shared workflow for commands that change a task's completion state. */
public abstract class TaskStateCommand extends Command {
    private final int taskIndex;

    /** Creates a state command for a validated zero-based task index. */
    protected TaskStateCommand(int taskIndex) {
        assert taskIndex >= 0 : "Task index must not be negative";
        this.taskIndex = taskIndex;
    }

    /** Applies the concrete completion-state change and returns the affected task. */
    protected abstract Task update(TaskList tasks, int taskIndex);
    /** Returns the concrete success message. */
    protected abstract String successMessage();

    /** Validates, updates, saves, and reports the selected task. */
    @Override
    public final void execute(TaskList tasks, Ui ui, Storage storage) {
        try {
            Task task = update(tasks, taskIndex);
            storage.save(tasks);
            ui.show(successMessage(), " " + task);
        } catch (IndexOutOfBoundsException e) {
            throw new TaskValidationException(
                    "That trail marker doesn't exist. Use list to check the available numbers.");
        }
    }
}
