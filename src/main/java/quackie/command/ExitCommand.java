package quackie.command;

import quackie.storage.Storage;
import quackie.task.TaskList;
import quackie.ui.Ui;

/**
 * Ends the current Quackie session.
 */
public class ExitCommand extends Command {
    /** Creates an exit command. */
    public ExitCommand() {
    }

    /** Displays the farewell message for the current session. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showBye();
    }

    /**
     * Indicates that this command terminates the session.
     *
     * @return {@code true} because this is an exit command
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
