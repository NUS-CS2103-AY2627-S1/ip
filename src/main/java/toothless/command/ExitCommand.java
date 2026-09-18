package toothless.command;

import toothless.storage.Storage;
import toothless.task.TaskList;
import toothless.ui.Ui;

/**
 * Displays Toothless's farewell and ends the current application session.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that ends the current Toothless session.
     */
    public ExitCommand() {
    }

    /**
     * Displays the farewell message for this session.
     *
     * @param taskList current tasks, which remain unchanged by this command
     * @param ui user interface used to display Toothless's farewell
     * @param storage configured storage, which is not needed by this command
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Identifies this command as the command that ends the application loop.
     *
     * @return true because this command exits the application
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
