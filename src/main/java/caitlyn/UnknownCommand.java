package caitlyn;

import java.util.List;

/**
 * A command representing input that Caitlyn does not recognize.
 */
public final class UnknownCommand extends Command {
    /**
     * Creates a command for unrecognized input.
     */
    public UnknownCommand() {
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    /**
     * Rejects the unrecognized command with a user-facing error.
     *
     * @param tasks the current task list, which is not changed.
     * @param ui the UI used by the surrounding application.
     * @throws CaitlynException always, because this command is not recognized.
     */
    @Override
    public void execute(List<Task> tasks, Ui ui) throws CaitlynException {
        throw new CaitlynException(
                "I humbly beg your pardon, master. I do not know how to carry out that command.");
    }
}
