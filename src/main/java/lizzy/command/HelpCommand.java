package lizzy.command;

import lizzy.storage.Storage;
import lizzy.task.TaskList;
import lizzy.ui.Ui;

/**
 * Displays a concise guide to Lizzy's supported commands.
 */
public class HelpCommand extends Command {
    /**
     * Creates a command that displays help.
     */
    public HelpCommand() {
    }

    /**
     * Displays command formats and their purposes.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
