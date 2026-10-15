package yourhand.commands;

import yourhand.exceptions.YourHandException;
import yourhand.storage.Storage;
import yourhand.tasks.TaskList;
import yourhand.ui.Ui;

/** Displays every task currently in the list. */
public class ListCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws YourHandException {
        ui.showTaskList(taskList);
    }
}
