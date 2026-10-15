package jason.command;

import jason.storage.Storage;
import jason.task.TaskList;
import jason.ui.Ui;

/**
 * Ends the application without changing the task list.
 */
public class ExitCommand extends Command {
    /**
     * Performs no state change because the application is exiting.
     *
     * @param tasks task list, which is unused.
     * @param ui user interface, which is unused.
     * @param storage storage, which is unused.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        // There is no work to do when exiting.
    }

    /**
     * Returns true because this command ends the application.
     *
     * @return true.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
