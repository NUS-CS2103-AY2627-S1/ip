package sallman.command;

import sallman.SallmanException;
import sallman.Storage;
import sallman.TaskList;
import sallman.Ui;

/**
 * One thing the user has asked the chatbot to do.
 * <p>
 * A command knows how to carry itself out, so the main loop can run any
 * command without knowing which one it is holding. Adding a new command means
 * adding a subclass here rather than extending a switch elsewhere.
 */
public abstract class Command {

    /**
     * Carries out this command.
     *
     * @param tasks   the task list to act on
     * @param ui      used to tell the user what happened
     * @param storage used to save the list when a command changes it
     * @throws SallmanException if the command cannot be carried out
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws SallmanException;

    /**
     * Returns whether the chatbot should stop after this command.
     * <p>
     * Only the exit command overrides this, so every other subclass is spared
     * having to say that it does not end the session.
     *
     * @return true if this command ends the session
     */
    public boolean isExit() {
        return false;
    }
}
