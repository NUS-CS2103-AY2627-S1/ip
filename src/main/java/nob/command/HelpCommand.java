package nob.command;

import nob.storage.Storage;
import nob.task.TaskList;
import nob.ui.Ui;

/**
 * Displays the commands that Nob supports.
 */
public class HelpCommand extends Command {
    /** Displays the help text. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
