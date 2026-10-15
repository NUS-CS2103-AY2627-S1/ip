package avon.command;

import avon.storage.Storage;
import avon.task.TaskList;
import avon.ui.Ui;

/**
 * Ends the current Avon session.
 */
public class ExitCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExit() {
        return true;
    }
}
