package potato.command;

import potato.storage.Storage;
import potato.tasklist.TaskList;
import potato.ui.Ui;

/**
 * Command to display all tasks in the list.
 */
public class ListCommand extends Command {

    /**
     * Executes the list command to show all current tasks to the user.
     *
     * @param tasks Task list to display.
     * @param ui User interface context for output generation.
     * @param storage Persistence layer.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}