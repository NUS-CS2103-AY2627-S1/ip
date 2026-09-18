package bibi.command;

import bibi.Storage;
import bibi.Ui;
import bibi.task.TaskList;

/**
 * Shows every task, numbered as the user would refer to them.
 */
public class ListCommand extends Command {
    /**
     * Creates a command that will show the whole task list.
     */
    public ListCommand() {
    }

    /**
     * Shows every task in order, or reports that the list is empty.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.isEmpty()) {
            ui.showMessage("Nothing on the list. I'll assume that's good news. "
                    + "Add something with todo, deadline or event.");
            return;
        }

        ui.showMessage("Here is everything on your list:");
        showAllTasks(tasks, ui);
    }
}
