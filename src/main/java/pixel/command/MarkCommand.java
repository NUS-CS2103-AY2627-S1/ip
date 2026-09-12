package pixel.command;

import pixel.storage.Storage;
import pixel.task.TaskList;
import pixel.ui.Ui;

/**
 * Marks a task as done.
 */
public class MarkCommand extends Command {
    private final int index;

    /**
     * Creates a command for a zero-based task index.
     *
     * @param index Zero-based task index.
     */
    public MarkCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (!tasks.isValidIndex(index)) {
            ui.showMessage("That task number does not exist.");
            return;
        }

        tasks.markAsDone(index);
        saveTasksSafely(tasks, ui, storage);
        ui.showMessage("Nice! I've marked this task as done:");
        ui.showMessage("  " + tasks.get(index));
    }
}
