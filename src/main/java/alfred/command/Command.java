package alfred.command;

import alfred.exception.AlfredException;
import alfred.storage.Storage;
import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * Represents an executable user command.
 */
public abstract class Command {
    /**
     * Performs this command using Alfred's task list, user interface, and storage.
     *
     * @param tasks current task list
     * @param ui user interface used to display responses
     * @param storage task storage used to persist changes
     * @throws AlfredException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws AlfredException;

    /** Returns whether executing this command exits Alfred. */
    public boolean isExit() {
        return false;
    }
}
