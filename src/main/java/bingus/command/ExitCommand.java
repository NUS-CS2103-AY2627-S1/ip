package bingus.command;

import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Ends the Bingus application after showing a farewell message.
 */
public class ExitCommand extends Command {

    @Override
    public CommandType getType() {
        return CommandType.EXIT;
    }

    /**
     * Returns the farewell message.
     *
     * @param tasks current task list, which is not changed.
     * @param ui user interface used to format the message.
     * @param storage persistent task storage, which is not changed.
     * @return farewell message.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws BingusException {
        return ui.getByeMessage();
    }
}
