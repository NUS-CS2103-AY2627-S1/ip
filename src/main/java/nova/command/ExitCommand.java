package nova.command;

import nova.storage.Storage;
import nova.task.TaskList;
import nova.ui.Ui;

/** Command action that ends the Nova session. */
public class ExitCommand extends CommandAction {
    /** Shows the farewell message. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showFarewell();
    }

    /** Returns true because this command ends the application. */
    @Override
    public boolean isExit() {
        return true;
    }
}
