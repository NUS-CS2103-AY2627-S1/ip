package matthew.command;

import matthew.storage.Storage;
import matthew.task.TaskList;
import matthew.ui.Ui;

/** Displays every task currently in the task list. */
public class ListCommand extends Command {

    /** Creates a list command. */
    public ListCommand() {
    }

    /**
     * Displays all tasks without modifying or saving the task list.
     *
     * @param tasks Task list to display.
     * @param ui User interface used to display the tasks.
     * @param storage Unused storage supplied by the command loop.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.getAll());
    }
}
