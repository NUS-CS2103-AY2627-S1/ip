package et.command;

import et.storage.Storage;
import et.task.TaskList;
import et.ui.Ui;

/**
 * Displays tasks in chronological order without changing their stored order.
 */
public class SortCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showSortedTaskList(tasks);
    }
}
