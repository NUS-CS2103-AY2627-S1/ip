package monkey.command;

import monkey.model.TaskList;
import monkey.storage.Storage;
import monkey.ui.Ui;

/** Command object for ending the Monkey session. */
public class ExitCommand extends CommandAction {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage("Bye! Keep swinging, and I hope to see you again soon!");
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
