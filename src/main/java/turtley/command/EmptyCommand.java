package turtley.command;

import turtley.model.TaskList;
import turtley.storage.Storage;
import turtley.ui.Ui;

/**
 * Responds to an empty input line.
 */
public class EmptyCommand extends Command {

    /**
     * Creates an empty command.
     */
    public EmptyCommand() {
    }

    /**
     * Displays the response for an empty command.
     *
     * @param tasks the application's task list, which is not modified.
     * @param ui the application's user interface.
     * @param storage the application's persistence service, which is not used.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showEmptyInput();
    }
}
