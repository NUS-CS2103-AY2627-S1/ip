package gongrilla.command;

import gongrilla.storage.Storage;
import gongrilla.task.TaskList;
import gongrilla.ui.Ui;

/**
 * Displays every task currently in the task list.
 */
public class ListCommand extends Command {
    /** Creates a command that displays the current task list. */
    public ListCommand() {
    }

    /**
     * Displays the current task list without modifying it.
     *
     * @param tasks task list to display.
     * @param ui user interface used to display the tasks.
     * @param storage task storage; unused because listing does not persist changes.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.asList());
    }
}
