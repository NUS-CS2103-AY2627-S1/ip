package sage.command;

import sage.core.TaskList;
import sage.storage.Storage;
import sage.ui.Ui;

/**
 * Terminates the application loop after displaying a farewell message.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that exits the application.
     */
    public ExitCommand() {
    }

    /**
     * Shows the exit message to the user.
     *
     * @param tasks the task list, which is not used by this command.
     * @param ui the user interface used to display the farewell message.
     * @param storage the storage system, which is not used by this command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showBye();
    }

    /**
     * Indicates that this command exits the application.
     *
     * @return true because the command exits the application.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
