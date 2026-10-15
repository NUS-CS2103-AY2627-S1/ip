package noms.command;

import noms.storage.Storage;
import noms.task.TaskList;
import noms.ui.Ui;

/**
 * Shows every task currently in the list, numbered from 1.
 */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.asList());
    }
}
