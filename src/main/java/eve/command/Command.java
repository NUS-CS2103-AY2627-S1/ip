package eve.command;

import eve.EveException;
import eve.Storage;
import eve.Ui;
import eve.task.TaskList;

/**
 * A user command that can be executed against the current task list. Each
 * concrete subclass knows how to carry out one specific command (adding a
 * task, listing them, exiting, etc.); {@link Parser#parse} decides which
 * one to build from a line of user input.
 */
public abstract class Command {
    /**
     * Carries out this command: updates {@code tasks} if needed, persists
     * the change via {@code storage} if needed, and reports the result
     * through {@code ui}.
     *
     * @throws EveException if the command can't be completed, e.g. an
     *      out-of-range task number.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws EveException;

    /** Returns whether this command should end the program's main loop. */
    public boolean isExit() {
        return false;
    }
}
