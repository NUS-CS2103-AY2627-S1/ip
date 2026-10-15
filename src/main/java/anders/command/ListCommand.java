package anders.command;

import anders.collection.TaskList;
import anders.storage.Storage;
import anders.ui.Ui;

/** Displays all tasks. */
public class ListCommand extends Command {

    /** Displays every task currently stored in the task list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
