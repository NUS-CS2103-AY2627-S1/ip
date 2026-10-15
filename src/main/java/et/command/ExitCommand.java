package et.command;

import et.storage.Storage;
import et.task.TaskList;
import et.ui.Ui;

/**
 * Ends the ET session after displaying its goodbye message.
 */
public class ExitCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExit() {
        return true;
    }
}
