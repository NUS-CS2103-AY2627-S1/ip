package aster.command;

import aster.exception.AsterException;
import aster.storage.Storage;
import aster.task.TaskList;
import aster.ui.Ui;

/**
 * One thing the user has asked Aster to do.
 *
 * <p>A command is built by the {@code Parser} and later carried out by
 * {@link #execute}. What the parser can check depends on the command: for a todo,
 * deadline, event, list, find or stats it has already checked the wording, while a
 * command that names a task by number carries that text unchecked and settles it when
 * it runs.
 * See {@link IndexedCommand} for why those checks are held back.
 *
 * <p>A command that changes the task list asks for it to be saved after the change has
 * been made and reported. Checks that refuse a command run before anything is changed,
 * so a refused command neither changes the list nor writes to the file. Saving itself
 * can still fail afterwards; when it does, the change stays in this session only and
 * the user is told so.
 */
public abstract class Command {
    /**
     * Creates a command; each subclass adds whatever that command needs to run.
     */
    protected Command() {
    }

    /**
     * Carries out this command.
     *
     * @param tasks the task list to read or change.
     * @param ui the user interface to report the outcome through.
     * @param storage the store to write the tasks to, for commands that change them.
     * @throws AsterException if the command cannot be carried out on the list as it
     *     stands, or if the changed list cannot be saved.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws AsterException;
}
