package lizzy.command;

import lizzy.storage.Storage;
import lizzy.task.TaskList;
import lizzy.ui.Ui;

/**
 * Ends the current Lizzy session.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that ends the session.
     */
    public ExitCommand() {
    }

    /**
     * Displays Lizzy's farewell.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Identifies this command as the session-ending command.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
