package minty.command;

import minty.storage.Storage;
import minty.task.TaskList;
import minty.ui.Ui;

/**
 * Tells Minty to end the current command loop.
 */
public class ExitCommand extends Command {

    /**
     * Performs no task operation because exiting is handled by {@link #isExit()}.
     *
     * @param tasks task list, which is not changed.
     * @param ui command-line interface, which is not used.
     * @param storage task storage, which is not used.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        // No action is needed before Minty exits.
    }

    /**
     * Indicates that Minty should end after this command.
     *
     * @return always {@code true}.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
