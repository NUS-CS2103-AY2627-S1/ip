package judey.command;

import judey.storage.Storage;
import judey.tasklist.TaskList;
import judey.ui.Ui;
/**
 * Represents a command that terminates the application execution
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