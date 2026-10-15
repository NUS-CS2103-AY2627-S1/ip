package dukey.command;

import dukey.storage.Storage;
import dukey.task.TaskList;
import dukey.ui.Ui;

/**
 * Shows all tasks in the task list.
 */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showList(tasks);
    }

    @Override
    public String getResponseStyleClass() {
        return STYLE_SEARCH;
    }
}
