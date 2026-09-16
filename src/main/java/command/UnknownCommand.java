package puyo.command;

import puyo.PuyoException;
import puyo.storage.Storage;
import puyo.task.TaskList;
import puyo.ui.Ui;

/**
 * Represents a command executed when user input is unrecognised.
 */
public class UnknownCommand extends Command {

    /**
     * Executes the command by throwing an exception indicating that the user command was unknown.
     *
     * @param tasks The list of tasks.
     * @param ui The user interface for interaction.
     * @param storage The storage handler.
     * @throws PuyoException Always thrown to inform the user of an invalid command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PuyoException {
        throw new PuyoException("Sorry! I don't understand that command!");
    }
}
