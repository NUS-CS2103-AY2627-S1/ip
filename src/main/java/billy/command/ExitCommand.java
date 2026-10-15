package billy.command;

import billy.storage.Storage;
import billy.task.TaskList;
import billy.ui.Ui;

/**
 * Ends the conversation.
 *
 * <p>Carrying it out does nothing at all: the farewell is printed once the
 * conversation has finished, not as part of this command, so that it appears
 * whether the user said goodbye or simply ran out of input.
 */
public class ExitCommand extends Command {

    /**
     * Does nothing at all.
     *
     * <p>The farewell is printed once the conversation has ended rather than
     * here, so that it appears whether the user said goodbye or simply ran out
     * of input.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        // Nothing to do. Saying so is isExit's job.
    }

    /** Returns true: this is the one command that ends the conversation. */
    @Override
    public boolean isExit() {
        return true;
    }
}
