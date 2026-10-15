package bingbong.command;

import bingbong.storage.Storage;
import bingbong.task.TaskList;
import bingbong.ui.Ui;

/**
 * bingbong.command.Command to display all tasks in the list to the user.
 */
public class ListCommand extends Command {

    /**
     * Loops across active collections to display individual items to users.
     *
     * @param tasks   The active TaskList tracking current items.
     * @param ui      The user interface formatting output blocks.
     * @param storage The storage file handler managing disk reads and writes.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.print("BingBong shows your list:");
        for (int i = 0; i < tasks.size(); i++) {
            ui.print((i + 1) + ". " + tasks.get(i).toString());
        }
    }
}
