package ev.command;

import ev.storage.Storage;
import ev.task.TaskList;
import ev.ui.Ui;

/**
 * Shows every task in the list, in order.
 */
public class ListCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasks(tasks);
    }
}
