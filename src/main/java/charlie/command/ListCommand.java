package charlie.command;

import charlie.storage.Storage;
import charlie.task.TaskList;
import charlie.ui.Ui;

/**
 * Displays every task in the task list.
 */
public class ListCommand extends Command {
    /**
     * Creates a list command.
     */
    public ListCommand() {
    }

    /**
     * Displays the current tasks in their list order with one-based numbering.
     *
     * @param tasks Task collection to display.
     * @param ui User interface used to display the tasks.
     * @param storage Storage collaborator, which this read-only command does not use.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.getSize() == 0) {
            ui.showMessage("Your task nest is empty.");
            return;
        }

        ui.showMessage("Here's what's currently in our task nest:");
        for (int i = 0; i < tasks.getSize(); i++) {
            ui.showMessage((i + 1) + "." + tasks.get(i));
        }
    }
}
