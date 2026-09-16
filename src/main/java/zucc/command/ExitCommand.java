package zucc.command;

import zucc.ZuccException;
import zucc.storage.Storage;
import zucc.task.TaskList;
import zucc.ui.Ui;

/**
 * Ends the current Zucc session after saying goodbye to the user.
 */
public final class ExitCommand extends Command {
    /** Creates an exit command awaiting values from Parser. */
    ExitCommand() {
        super("bye");
    }

    /**
     * Shows the farewell message.
     *
     * @param tasks task collection for the current session.
     * @param ui user interface through which the farewell is shown.
     * @param storage persistent storage; unchanged by this command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZuccException {
        requireNoArgument();
        ui.showMessage("Bye. Hope to see you again soon!");
    }

    /**
     * Identifies this command as the command that ends the session.
     *
     * @return {@code true}.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
