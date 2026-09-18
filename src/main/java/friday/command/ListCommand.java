package friday.command;

import friday.model.TaskList;
import friday.storage.Storage;
import friday.ui.Ui;

/**
 * Displays all tasks in the task list.
 */
public class ListCommand extends Command {
    /**
     * Creates a list command.
     */
    public ListCommand() {
    }

    /**
     * Displays every task currently in the list.
     *
     * @param tasks Current task list to display.
     * @param ui UI used to display the tasks.
     * @param storage Storage available to the command, which is unused.
     */
    @Override
    public void execute(TaskList tasks, TaskList archivedTasks, Ui ui, Storage storage, Storage archiveStorage) {
        ui.showTaskList(tasks);
    }
}
