package noms.command;

import noms.storage.Storage;
import noms.task.TaskList;
import noms.ui.Ui;

/**
 * Says goodbye and signals that Noms should stop reading commands.
 */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
