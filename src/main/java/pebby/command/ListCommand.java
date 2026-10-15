package pebby.command;

import pebby.storage.Storage;
import pebby.task.TaskList;
import pebby.ui.Ui;

/**
 * Displays every task currently held by Pebby.
 */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        StringBuilder taskList = new StringBuilder("Here are the tasks in your list:\n");
        for (int index = 0; index < tasks.size(); index++) {
            taskList.append(index + 1).append(". ").append(tasks.get(index)).append('\n');
        }
        ui.show(taskList.toString());
    }
}
