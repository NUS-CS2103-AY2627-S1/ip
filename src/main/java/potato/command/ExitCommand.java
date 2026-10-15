package potato.command;

import potato.storage.Storage;
import potato.tasklist.TaskList;
import potato.ui.Ui;

/**
 * Command to terminate the application session.
 */
public class ExitCommand extends Command {

    /**
     * Executes the exit command sequence by showing a goodbye message.
     *
     * @param tasks Task list context.
     * @param ui User interface for displaying output.
     * @param storage Persistence layer.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Indicates whether execution of this command terminates the application.
     *
     * @return {@code true} to signal program termination.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}