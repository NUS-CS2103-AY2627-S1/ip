package beemo.command;

import beemo.storage.Storage;
import beemo.task.TaskList;
import beemo.ui.Ui;

/**
 * Ends the current Beemo session.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that exits Beemo.
     */
    public ExitCommand() {
    }

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
