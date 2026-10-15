package bob.command;

import bob.exception.BobException;
import bob.storage.Storage;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Represents a command to exit the application.
 */
public class ExitCommand extends Command {

    /**
     * Constructs an ExitCommand.
     */
    public ExitCommand() {
    }

    /**
     * Executes the exit command by displaying the goodbye message to the user.
     *
     * @param tasks   the list of tasks
     * @param ui      the user interface handler
     * @param storage the storage handler
     * @throws BobException if an error occurs during execution
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage<TaskList> storage) throws BobException {
        assertExecutionDependencies(tasks, ui, storage);
        ui.setGoodbye();
    }

    /**
     * Returns true to signal that the application should terminate.
     *
     * @return true indicating an exit command
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
