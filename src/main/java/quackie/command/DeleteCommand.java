package quackie.command;

import quackie.storage.Storage;
import quackie.task.Task;
import quackie.task.TaskList;
import quackie.ui.Ui;

/**
 * Deletes one task from the task list.
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Creates a delete command for a zero-based task index.
     *
     * @param index the zero-based task index, or {@code -1} when invalid
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (index < 0) {
            ui.showError("Please provide a valid task number.");
            return;
        }
        Task removedTask = tasks.delete(index);
        saveTasks(tasks, ui, storage);
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
