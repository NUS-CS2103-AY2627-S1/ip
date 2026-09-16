package turtley.command;

import turtley.model.TaskList;
import turtley.storage.Storage;
import turtley.ui.Ui;

/**
 * Terminates the application.
 */
public class ExitCommand extends Command {

    /**
     * Creates an exit command.
     */
    public ExitCommand() {
    }

    /**
     * Displays the normal shutdown message.
     *
     * @param tasks the application's task list, which is not modified.
     * @param ui the application's user interface.
     * @param storage the application's persistence service, which is not used.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Indicates that this command terminates the application.
     *
     * @return always {@code true}
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
