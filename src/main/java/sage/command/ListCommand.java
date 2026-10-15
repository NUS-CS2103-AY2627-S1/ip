package sage.command;

import sage.core.TaskList;
import sage.storage.Storage;
import sage.ui.Ui;

/**
 * Displays the current list of tasks in the user interface.
 */
public class ListCommand extends Command {
    /**
     * Creates a command that lists all tasks.
     */
    public ListCommand() {
    }

    /**
     * Prints all tasks currently stored in the list.
     *
     * @param tasks the task list to display.
     * @param ui the user interface used to display the list.
     * @param storage the storage system, which is not used by this command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.getTasks());
    }
}
