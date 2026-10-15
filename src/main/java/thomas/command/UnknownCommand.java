package thomas.command;

import thomas.ThomasException;
import thomas.storage.Storage;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * Represents a command that Thomas does not recognize.
 */
public class UnknownCommand extends Command {
    /**
     * Reports that the typed command is not supported.
     *
     * @param tasks unused for this command
     * @param ui unused for this command
     * @param storage unused for this command
     * @throws ThomasException always, with a hint to try again
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ThomasException {
        throw new ThomasException("I doono what that is, try again mate :>");
    }
}
