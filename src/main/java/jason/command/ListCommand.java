package jason.command;

import jason.storage.Storage;
import jason.task.TaskList;
import jason.ui.Ui;

/**
 * Displays the current task list.
 */
public class ListCommand extends Command {
    /**
     * Displays the supplied task list without changing it.
     *
     * @param tasks task list to display.
     * @param ui user interface used to display the list.
     * @param storage storage, which is unused because listing does not mutate tasks.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
