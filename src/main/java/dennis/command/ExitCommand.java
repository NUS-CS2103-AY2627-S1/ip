package dennis.command;

import dennis.storage.Storage;
import dennis.task.TaskList;
import dennis.ui.Ui;

/**
 * Says goodbye and signals that the program should stop.
 */
public class ExitCommand extends Command {
    /** Creates the exit command. */
    public ExitCommand() {
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
        ui.showLine();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
