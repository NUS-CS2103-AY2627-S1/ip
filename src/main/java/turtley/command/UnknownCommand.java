package turtley.command;

import turtley.model.TaskList;
import turtley.storage.Storage;
import turtley.ui.Ui;

/**
 * Responds to an unrecognized input line.
 */
public class UnknownCommand extends Command {

    /**
     * Creates an unknown command.
     */
    public UnknownCommand() {
    }

    /**
     * Displays the response for an unrecognized command.
     *
     * @param tasks the application's task list, which is not modified.
     * @param ui the application's user interface.
     * @param storage the application's persistence service, which is not used.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showUnknownCommand();
    }
}
