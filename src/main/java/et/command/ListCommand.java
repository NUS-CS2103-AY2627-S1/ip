package et.command;

import et.storage.Storage;
import et.task.TaskList;
import et.ui.Ui;

/**
 * Displays every task currently managed by ET.
 */
public class ListCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
