package kong.command;

import kong.storage.Storage;
import kong.task.TaskList;
import kong.ui.Ui;

/**
 * Ends the chatbot session.
 */
public class ExitCommand extends Command {
    /** Creates an exit command. */
    public ExitCommand() {
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExit() {
        return true;
    }
}
