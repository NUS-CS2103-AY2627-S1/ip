package fifi.command;

import fifi.Storage;
import fifi.TaskList;
import fifi.Ui;

/**
 * Shows all tasks in the task list.
 */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showResponse(String.format("Here are the tasks in your list:%s", tasks.toDisplayString()));
    }
}
