package rudra.command;

import java.util.ArrayList;

import rudra.storage.Storage;
import rudra.task.Task;
import rudra.ui.Ui;

/**
 * Shows all tasks currently in the list.
 */
public class ListCommand extends Command {
    /**
     * Shows the current list of tasks without modifying stored data.
     *
     * @param tasks Current task list.
     * @param ui UI helper used to show output.
     * @param storage Storage helper kept for a consistent command interface.
     */
    @Override
    public void execute(ArrayList<Task> tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
