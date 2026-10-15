package xiaozhi.command;

import xiaozhi.storage.Storage;
import xiaozhi.task.TaskList;
import xiaozhi.ui.Ui;

/**
 * Signals that XiaoZhi should exit its main loop.
 */
public class ExitCommand extends Command {

    /**
     * Does nothing; XiaoZhi prints the farewell itself once the loop ends.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage, CommandHistory history) {
        // Nothing to do here; XiaoZhi prints the farewell after the loop ends.
    }

    /**
     * Returns {@code true}, always - this is the one command that ends the loop.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
