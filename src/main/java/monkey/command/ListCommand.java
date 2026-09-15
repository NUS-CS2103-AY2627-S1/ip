package monkey.command;

import monkey.model.TaskList;
import monkey.storage.Storage;
import monkey.ui.Ui;

/** Command object for displaying the current task list. */
public class ListCommand extends CommandAction {
    /** Creates a command that displays all current tasks. */
    public ListCommand() { }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.isEmpty()) {
            ui.showMessage("There are no tasks in your list.");
            return;
        }

        ui.showMessage("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            ui.showMessage((i + 1) + "." + tasks.get(i));
        }
    }
}
