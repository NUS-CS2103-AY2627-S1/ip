package sine.command;

import sine.storage.Storage;
import sine.task.TaskList;
import sine.ui.Ui;

/**
 * Responds to input that does not match a supported command.
 */
public class UnknownCommand extends Command {
    /**
     * Shows the response for an unrecognized command.
     *
     * @param tasks current task list
     * @param ui console user interface
     * @param storage task persistence service
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showUnknownCommand();
    }
}
