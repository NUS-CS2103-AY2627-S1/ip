package habpyduck.command;

import habpyduck.storage.Storage;
import habpyduck.task.TaskList;
import habpyduck.ui.Ui;

/**
 * Ends the current chatbot session.
 */
public class ExitCommand extends Command {
    /**
     * Does nothing because the farewell message is shown by the main loop.
     *
     * @param tasks the task list.
     * @param ui the UI.
     * @param storage the storage.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        // Nothing to execute; isExit tells the main loop to stop.
    }

    /**
     * Returns true because this command exits the program.
     *
     * @return true.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
