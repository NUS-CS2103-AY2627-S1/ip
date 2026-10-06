package caesar.command;

import caesar.exception.CaesarException;
import caesar.storage.Storage;
import caesar.task.TaskList;
import caesar.ui.Ui;

/** Displays the available command formats. */
public class HelpCommand extends Command {
    /** Displays the available command formats through the current user interface. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws CaesarException {
        ui.showHelp();
    }
}
