package goop.command;

import goop.storage.Storage;
import goop.task.TaskList;
import goop.ui.Ui;

/**
 * Represents a request to exit the application.
 */
public class ExitCommand extends Command {
    /**
     * Creates an exit command.
     */
    public ExitCommand() {
    }

    /**
     * Displays the farewell message.
     *
     * @param tasks Current task list, which is not changed.
     * @param ui User interface used to display the farewell message.
     * @param storage Task storage, which is not accessed.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Identifies this command as the application exit command.
     *
     * @return True.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
