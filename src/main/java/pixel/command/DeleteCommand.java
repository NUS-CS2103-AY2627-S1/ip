package pixel.command;

import pixel.storage.Storage;
import pixel.task.Task;
import pixel.task.TaskList;
import pixel.ui.Ui;

/**
 * Deletes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Creates a command for a zero-based task index.
     *
     * @param index Zero-based task index.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (!tasks.isValidIndex(index)) {
            ui.showMessage("That task number does not exist.");
            return;
        }

        Task deletedTask = tasks.delete(index);
        saveTasksSafely(tasks, ui, storage);
        ui.showMessage("Noted. I've removed this task:");
        ui.showMessage("  " + deletedTask);
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }
}
