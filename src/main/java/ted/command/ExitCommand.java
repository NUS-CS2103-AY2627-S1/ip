package ted.command;

import ted.Storage;
import ted.Ui;
import ted.task.TaskList;

/**
 * Ends the conversation.
 */
public class ExitCommand extends Command {
    /** Creates a command that ends the conversation. */
    public ExitCommand() {
    }

    /** Does nothing: leaving needs no work beyond stopping the loop. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        // Nothing to do: Ted shows the goodbye itself when it sees isExit(),
        // so that the goodbye replaces an ordinary reply.
    }

    /**
     * {@inheritDoc}
     *
     * @return always {@code true}: this is the command that ends the conversation.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
