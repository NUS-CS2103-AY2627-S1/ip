package beemo.command;

import beemo.storage.Storage;
import beemo.task.TaskList;
import beemo.ui.Ui;

/**
 * Displays guidance for Beemo's supported commands.
 */
public class HelpCommand extends Command {
    /**
     * Creates a command that displays usage guidance.
     */
    public HelpCommand() {
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
