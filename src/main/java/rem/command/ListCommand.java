package rem.command;

import rem.storage.Storage;
import rem.task.TaskList;
import rem.ui.Ui;

/**
 * Displays every task in the task list.
 */
public class ListCommand extends Command {
    /**
     * Displays the tasks in their current order.
     *
     * @param tasks Tasks managed by Rem.
     * @param ui User interface used to display the tasks.
     * @param storage Storage available to commands that need persistence.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage("Hmm... what to do now?");
        if (tasks.size() == 0) {
            ui.showMessage("No tasks yet. Got something for us to do?");
        }
        ui.showNumberedTasks(tasks.getTasks());
    }
}
