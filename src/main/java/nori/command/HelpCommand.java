package nori.command;

import nori.storage.Storage;
import nori.task.TaskList;
import nori.ui.Ui;

/**
 * Displays Nori's supported commands.
 */
class HelpCommand extends Command {
    /** Creates a help command. */
    HelpCommand() {
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
