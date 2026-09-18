package alfred.command;

import alfred.AlfredException;
import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * An executable user command produced by {@link alfred.parser.Parser}.
 */
public abstract class Command {
    /**
     * Carries out this command against the given task list and UI.
     *
     * @param tasks Task list to read or update.
     * @param ui User interface for replies.
     * @throws AlfredException If the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui) throws AlfredException;

    /**
     * Returns {@code true} if Alfred should exit after this command.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Returns {@code true} if this command changes tasks that must be saved.
     */
    public boolean isMutating() {
        return false;
    }
}
