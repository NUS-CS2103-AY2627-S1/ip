package lebronjames.command;

import lebronjames.storage.Storage;
import lebronjames.task.TaskList;
import lebronjames.ui.Ui;

/**
 * Shows every task in the list.
 */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.asList());
    }
}
