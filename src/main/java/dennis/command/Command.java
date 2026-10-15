package dennis.command;

import dennis.DennisException;
import dennis.storage.Storage;
import dennis.task.TaskList;
import dennis.ui.Ui;

/**
 * A single user instruction, ready to be carried out.
 *
 * <p>Each concrete subclass knows how to perform one kind of command
 * (add a task, delete a task, exit, ...). {@link dennis.parser.Parser#parse(String)}
 * turns a line of input into the right {@code Command}, and {@code Dennis}
 * then simply calls {@link #execute} without needing to know which command
 * it received. Adding a new command means writing a new subclass rather
 * than extending a growing {@code switch}.</p>
 */
public abstract class Command {
    /** For use by subclasses; there is no per-command state to initialise here. */
    protected Command() {
    }

    /**
     * Carries out this command.
     *
     * @param tasks   the task list to read or modify
     * @param ui      used to show results and messages to the user
     * @param storage used to persist the task list when it changes
     * @throws DennisException if the command cannot be completed, e.g. a
     *                         task number that does not refer to a task
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws DennisException;

    /**
     * Returns whether the program should stop after this command runs.
     *
     * @return {@code true} only for the exit command; {@code false} for
     *         every other command (the default)
     */
    public boolean isExit() {
        return false;
    }
}
