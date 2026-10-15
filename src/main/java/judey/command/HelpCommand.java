package judey.command;

import judey.exception.JudeyException;
import judey.storage.Storage;
import judey.tasklist.TaskList;
import judey.ui.Ui;

/** Represents a command that displays usage guidance for Judey. */
public class HelpCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws JudeyException {
        ui.showHelp();
    }
}
