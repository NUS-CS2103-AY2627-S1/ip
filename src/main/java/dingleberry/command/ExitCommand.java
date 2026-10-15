package dingleberry.command;

import dingleberry.model.TaskList;
import dingleberry.persistence.Storage;
import dingleberry.ui.Ui;

/** Signals that the main loop should stop after the goodbye message prints. */
public class ExitCommand extends Command {
    /**
    * Performs no mutation because the command's exit flag represents exiting.
     *
     * @param tasks the current task list, unchanged by this command
     * @param ui the UI used for any future output
     * @param storage the persistence layer, unchanged by this command
     */
    @Override
    public final void execute(final TaskList tasks, final Ui ui,
                              final Storage storage) {
        // Exiting has no effect on the task list, UI, or storage.
    }

    /**
    * Indicates that the application should leave the main input loop.
     *
     * @return true because this command terminates the program
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
