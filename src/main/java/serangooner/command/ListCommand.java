package serangooner.command;

import serangooner.storage.Storage;
import serangooner.task.TaskList;
import serangooner.ui.Ui;

/**
 * Shows the whole task list.
 */
public class ListCommand extends Command {
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        return ui.formatTasks(tasks.getEntries());
    }
}
