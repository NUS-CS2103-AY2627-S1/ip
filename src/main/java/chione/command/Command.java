package chione.command;

import chione.ChioneException;
import chione.Storage;
import chione.Ui;
import chione.task.TaskList;

/**
 * Something the user has asked Chione to do.
 *
 * <p>A {@link CommandType} is a word Chione recognizes; a {@code Command} is a
 * particular request, already understood and holding whatever it needs to be
 * carried out — the task to add, or the position to delete. Turning a line of
 * input into one of these is {@link chione.Parser Parser}'s job; carrying it out is this
 * class's.
 *
 * <p>Because each command knows how to carry itself out, {@link chione.Chione Chione} no
 * longer needs a branch per command. Adding a command means adding a subclass
 * here and a case in {@link chione.Parser Parser}, and nothing in the main loop changes.
 */
public abstract class Command {
    /**
     * Allows subclasses to be created.
     *
     * <p>A command holds only whatever its own subclass needs, so there is
     * nothing to set up here.
     */
    protected Command() {
    }

    /**
     * Carries out this command.
     *
     * <p>Each of the three collaborators is handed in rather than looked up, so a
     * command can be carried out against any task list, any user interface and
     * any save file — which is also what makes one testable on its own.
     *
     * @param tasks   the task list to act on
     * @param ui      how to tell the user what happened
     * @param storage where to record a change to the task list
     * @throws ChioneException if the command cannot be carried out
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws ChioneException;

    /**
     * Reports whether the conversation should end after this command.
     *
     * <p>Only {@link ExitCommand} answers yes, so the answer is given once here
     * rather than repeated in every other subclass.
     *
     * @return {@code true} if Chione should stop reading commands
     */
    public boolean isExit() {
        return false;
    }
}
