package avon.command;

import avon.storage.Storage;
import avon.task.TaskList;
import avon.ui.Ui;

/**
 * Displays every task in Avon's task list.
 */
public class ListCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) {
        ui.showTaskList(taskList);
    }
}
