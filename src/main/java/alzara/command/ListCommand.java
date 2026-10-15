package alzara.command;

import alzara.storage.Storage;
import alzara.task.TaskList;
import alzara.ui.Ui;

/**
 * Displays every task currently in the task list.
 */
public class ListCommand extends Command {
    /**
     * Prints every task currently in {@code memory}. Read-only, so {@code storage} is unused.
     */
    @Override
    public void execute(TaskList memory, Ui ui, Storage storage) {
        ui.showTaskList(memory.getTasks());
    }
}
