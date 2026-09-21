package peter.command;

import peter.storage.Storage;
import peter.task.TaskList;
import peter.ui.Ui;

/**
 * Says goodbye and ends the program.
 */
public class ExitCommand extends Command {

    /**
     * {@inheritDoc}
     *
     * <p>Only says goodbye. Every earlier change was already saved by the
     * command that made it, so there is nothing to save on the way out.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Returns {@code true}, which is what stops the main loop. This is the
     * only command that overrides the inherited {@code false}.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
