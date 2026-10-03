package ramly.command;

import ramly.model.TaskList;
import ramly.storage.Storage;
import ramly.ui.Ui;

/** Command that displays all tasks currently in the task list. */
public class ListCommand extends Command {
    /** Displays the current tasks or an empty-list message. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.size() == 0) {
            ui.show("Your trail is clear. Add a task when you're ready!");
            return;
        }
        ui.show("Here's your trail ahead:");
        for (int i = 0; i < tasks.size(); i++) {
            ui.show(i + 1 + "." + tasks.get(i));
        }
    }
}
