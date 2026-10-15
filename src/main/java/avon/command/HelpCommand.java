package avon.command;

import avon.storage.Storage;
import avon.task.TaskList;
import avon.ui.Ui;

/**
 * Displays guidance for every command Avon supports.
 */
public class HelpCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
