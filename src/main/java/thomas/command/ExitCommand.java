package thomas.command;

import thomas.storage.Storage;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * Ends the Thomas application.
 */
public class ExitCommand extends Command {
    /**
     * Shows the goodbye message before Thomas stops.
     *
     * @param tasks unused for this command
     * @param ui user interface used for output
     * @param storage unused for this command
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Signals that the command loop should stop.
     *
     * @return always {@code true}
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
