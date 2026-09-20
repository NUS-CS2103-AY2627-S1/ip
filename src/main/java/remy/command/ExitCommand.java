package remy.command;

import remy.storage.Storage;
import remy.task.TaskList;
import remy.ui.Ui;

/**
 * Ends the application loop.
 *
 * @author LEE-wz
 */
public class ExitCommand extends Command {
    /**
     * Perform exit command
     *
     * @param tasks task collection to operate on
     * @param ui user interface used to display results
     * @param storage persistence service used to save changes
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        // Ending the application does not change tasks or require output here.
    }

    /**
     * Returns whether this command ends the application loop.
     *
     * @return true when the application should exit
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
