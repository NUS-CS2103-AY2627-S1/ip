package sandrone.command;

import sandrone.storage.Storage;
import sandrone.task.TaskList;
import sandrone.ui.Ui;

/**
 * Represents the command that exits the chatbot.
 */
public class ExitCommand extends Command {
    /**
     * Does not change application state before exiting.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        // The command loop handles the farewell message after this command exits.
    }

    /**
     * Returns whether the command ends the command loop.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
