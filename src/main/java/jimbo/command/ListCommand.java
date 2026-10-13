package jimbo.command;

import jimbo.Storage;
import jimbo.TaskList;
import jimbo.ui.Ui;

/**
 * Command to list all tasks.
 */
public class ListCommand extends Command {
    @Override
    public String execute(Ui ui, TaskList tasks, Storage storage) {
        assert tasks != null : "tasks should not be null";

        if (tasks.isEmpty()) {
            return "wow you're really free";
        }

        return "you should probably do these soon\n" + tasks.listAll();
    }
}
