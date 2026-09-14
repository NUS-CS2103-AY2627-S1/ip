package nova.command;

import nova.NovaException;
import nova.Storage;
import nova.TaskList;
import nova.Ui;

/**
 * A single instruction from the user, ready to be carried out.
 *
 * <p>Parsing and execution are deliberately separated: {@link nova.Parser} turns a
 * line of text into the right subclass of this class, and the main loop only
 * has to call {@link #execute}. Adding a new command therefore means adding a
 * class rather than another branch in a growing switch.
 */
public abstract class Command {

    /** Restricts construction to subclasses. */
    protected Command() {
    }

    /**
     * Carries out this command.
     *
     * @param tasks   the task list to read or modify
     * @param ui      used to report the outcome to the user
     * @param storage used to persist the list when it changes
     * @throws NovaException if the command cannot be carried out
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException;

    /**
     * Returns whether the chatbot should stop after this command.
     *
     * @return true only for the exit command
     */
    public boolean isExit() {
        return false;
    }
}
