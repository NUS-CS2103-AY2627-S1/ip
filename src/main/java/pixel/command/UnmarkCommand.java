package pixel.command;

import pixel.storage.Storage;
import pixel.task.TaskList;
import pixel.ui.Ui;

/**
 * Marks a task as not done.
 */
public class UnmarkCommand extends Command {
    private final int index;

    /**
     * Creates a command for a zero-based task index.
     *
     * @param index Zero-based task index.
     */
    public UnmarkCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (!tasks.isValidIndex(index)) {
            ui.showMessage("That task number does not exist.");
            return;
        }

        tasks.markAsNotDone(index);
        saveTasksSafely(tasks, ui, storage);
        ui.showMessage("OK, I've marked this task as not done yet:");
        ui.showMessage("  " + tasks.get(index));
    }
}
