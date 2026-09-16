package lebronjames.command;

import lebronjames.storage.Storage;
import lebronjames.task.TaskList;
import lebronjames.ui.Ui;

/**
 * Says goodbye and ends the program.
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
