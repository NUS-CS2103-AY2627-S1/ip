package quackie.command;

import quackie.storage.Storage;
import quackie.task.Task;
import quackie.task.TaskList;
import quackie.ui.Ui;

/**
 * Replaces the details of one task without changing its list position or completion status.
 */
public class UpdateCommand extends Command {
    private final int index;
    private final Task replacement;

    /**
     * Creates an update command for a parsed replacement task.
     *
     * @param index the zero-based index of the task to update
     * @param replacement the task containing the new details
     */
    public UpdateCommand(int index, Task replacement) {
        this.index = index;
        this.replacement = replacement;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.containsExcept(replacement, index)) {
            ui.showError("That task is already in your list.");
            return;
        }
        Task updatedTask = tasks.update(index, replacement);
        saveTasks(tasks, ui, storage);
        ui.showTaskUpdated(updatedTask);
    }
}
