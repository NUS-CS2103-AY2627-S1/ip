package zen.command;

import zen.ZenException;
import zen.storage.Storage;
import zen.task.TaskList;
import zen.ui.Ui;

/** Represents a command keyword that is not supported. */
public class UnknownCommand extends Command {
    /** Reports that the entered command is unsupported. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZenException {
        throw new ZenException("I don't understand that command. Please try again.");
    }
}
