package walter.command;

import walter.storage.Storage;
import walter.task.TaskList;
import walter.ui.Ui;

/**
 * Displays the current task list without changing persisted state.
 */
public class ListCommand extends Command {
    /**
     * Displays all current tasks without modifying or saving them.
     *
     * @param tasks Task list to display.
     * @param ui User interface used to display the tasks.
     * @param storage Storage component supplied by the application loop; it is not used.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.getTasks());
    }
}
