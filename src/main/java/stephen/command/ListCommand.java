package stephen.command;

import stephen.storage.Storage;
import stephen.task.TaskList;
import stephen.ui.Ui;

/** Displays every task in the task list. */
public class ListCommand extends Command {
    /** Creates a command that displays the task list. */
    public ListCommand() {
    }

    /**
     * Displays all tasks in their current order.
     *
     * @param tasks task list to display
     * @param ui console UI used to display the list
     * @param storage persistence service; not used by this command
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
