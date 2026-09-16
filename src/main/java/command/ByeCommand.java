package puyo.command;

import puyo.PuyoException;
import puyo.storage.Storage;
import puyo.task.TaskList;
import puyo.ui.Ui;

/**
 * Represents a command to exit the application.
 */
public class ByeCommand extends Command {

    /**
     * Executes the command by saving current tasks and displaying a farewell message.
     *
     * @param tasks The list of tasks.
     * @param ui The user interface for interaction.
     * @param storage The storage handler for saving tasks.
     * @throws PuyoException If an error occurs during saving.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PuyoException {
        storage.save(tasks);
        ui.showBye();
    }

    /**
     * Indicates that this command will terminate the application.
     *
     * @return {@code true} to signal exit execution.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
