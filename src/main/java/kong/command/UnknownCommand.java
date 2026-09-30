package kong.command;

import kong.exception.KongException;
import kong.storage.Storage;
import kong.task.TaskList;
import kong.ui.Ui;

/**
 * Represents a command word that Kong does not understand.
 */
public class UnknownCommand extends Command {
    /** Creates a command representing unrecognized input. */
    public UnknownCommand() {
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KongException {
        throw new KongException("Confound it! I do not recognise that command in my lexicon.");
    }
}
