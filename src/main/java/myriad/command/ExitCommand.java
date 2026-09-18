package myriad.command;

import myriad.storage.Storage;
import myriad.task.TaskList;
import myriad.ui.Ui;

/**
 * Ends the session. Does nothing when executed: the farewell is shown by
 * {@code Myriad} once the command loop has stopped, so that it happens
 * exactly once whether the user typed {@code bye} or the input simply ran
 * out.
 */
public class ExitCommand extends Command {

    /** Creates the command {@code bye} asks for; it takes no arguments. */
    public ExitCommand() {
    }

    /**
     * Does nothing: this command's whole effect is {@link #isExit()}.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        // Nothing to do: isExit() below is what stops the command loop.
    }

    /**
     * Returns true, which is what stops {@code Myriad}'s command loop.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
