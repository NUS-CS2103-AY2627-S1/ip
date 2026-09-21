package pdd.command;

import pdd.PDDException;
import pdd.storage.Storage;
import pdd.task.TaskList;
import pdd.ui.Ui;

/**
 * Represents one user command as an object: the data it needs (parsed by
 * {@code Parser}) plus the behavior of carrying it out. This lets
 * {@code PDD}'s main loop stay a single generic
 * "parse, execute, check isExit" cycle instead of a per-command switch.
 */
public abstract class Command {
    /** Carries out this command against the given collaborators. */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws PDDException;

    /** Whether this command should end the program's main loop. Only {@code ExitCommand} overrides this. */
    public boolean isExit() {
        return false;
    }
}
