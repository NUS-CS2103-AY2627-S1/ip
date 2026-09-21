package quackie.command;

import quackie.storage.Storage;
import quackie.task.TaskList;
import quackie.ui.Ui;

/**
 * Marks one task as done.
 */
public class MarkCommand extends Command {
    private final int index;

    /**
     * Creates a mark command for a zero-based task index.
     *
     * @param index the zero-based task index, or {@code -1} when invalid
     */
    public MarkCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (index < 0) {
            ui.showError("Please provide a valid task number.");
            return;
        }
        tasks.markAsDone(index);
        saveTasks(tasks, ui, storage);
        ui.showTaskMarked(tasks.get(index));
    }
}
