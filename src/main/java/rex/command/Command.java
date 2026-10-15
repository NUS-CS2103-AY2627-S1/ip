package rex.command;

import rex.RexException;
import rex.TaskList;
import rex.Ui;

/**
 * One command the user gave, ready to be carried out.
 *
 * A command is built from the user's input before anything is done with it,
 * so each subclass holds whatever that command needs — a task to add, a task
 * number, a day to look at — and knows how to carry itself out. Asking the
 * command to run itself is what lets the main loop treat every command alike
 * instead of choosing between them.
 */
public abstract class Command {
    /**
     * Carries out this command.
     *
     * @param tasks the task list to act on.
     * @param ui used to show the user what happened.
     * @throws RexException if the command cannot be carried out, e.g. it names
     *     a task number that no task has.
     */
    public abstract void execute(TaskList tasks, Ui ui) throws RexException;

    /**
     * Returns true if the session should end after this command. Only the
     * exit command says yes, so the default here is no.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Returns true if this command changed the task list, and so requires the
     * tasks to be saved afterwards. Commands that only look at the list say
     * no, which is the default.
     *
     * Saving is left to the caller rather than done here so that the one
     * message shown when a save fails stays in one place, instead of being
     * repeated in every command that changes something.
     */
    public boolean isTaskListChanged() {
        return false;
    }
}
