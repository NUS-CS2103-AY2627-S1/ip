package thomas.command;

import java.io.IOException;

import thomas.ThomasException;
import thomas.storage.Storage;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * One command, understood and ready to be carried out.
 * <p>
 * A {@code Command} is built by {@link thomas.parser.Parser} from a line the
 * user typed, with whatever arguments that command takes already read and
 * checked. Carrying it out is then {@link #execute} -- so the work of
 * understanding a line and the work of doing what it asks are separated, and
 * neither has to be read to follow the other.
 * <p>
 * Each command is its own subclass rather than a branch of a switch. What that
 * buys is that everything about a command sits in one file:
 * {@link thomas.Thomas} asks for a command and runs it without knowing which
 * one it got, and adding a new one means writing a class rather than editing
 * the loop that runs them all.
 * <p>
 * The collaborators are handed to {@link #execute} rather than held as fields,
 * because a command is built fresh for every line and would otherwise carry
 * references it uses once. It also keeps plain what each command is allowed to
 * touch: the tasks, the screen, the save file, and the record of how to undo.
 */
public abstract class Command {
    /**
     * Carries out this command and says what happened.
     * <p>
     * The message is returned rather than printed, because the same words have
     * to reach a console session and a dialog bubble and a command has no
     * business knowing which it is talking to. Wording them is still
     * {@link Ui}'s, which is what {@code ui} is for; showing them belongs to
     * whoever asked for the command to be run.
     *
     * A command that changes the list also records how to reverse it, so that
     * {@code undo} can put the list back. The record is pushed after the change
     * has happened, which is what leaves the history untouched by a command that
     * throws. A command that changes nothing pushes nothing, and so is passed
     * over by {@code undo} without having to say that it should be.
     *
     * @param tasks The task list to read or change.
     * @param ui How to word what happened.
     * @param storage Where to write the tasks when they change.
     * @param history Where a command that changes the list records how to
     *                reverse it.
     * @return What to tell the user, or the empty string for a command with
     *         nothing to say.
     * @throws ThomasException If the command cannot be carried out, for example
     *                         because it names a task that does not exist --
     *                         which cannot be known until the list is in hand.
     */
    public abstract String execute(TaskList tasks, Ui ui, Storage storage, History history)
            throws ThomasException;

    /**
     * Returns whether the chatbot should stop after this command.
     * <p>
     * False for almost every command, so that is the answer here and only
     * {@link ExitCommand} overrides it. Asking the command means the read loop
     * never tests which command it is holding.
     *
     * @return True if this command ends the session.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves the task list, warning about a failure instead of crashing.
     * <p>
     * Inherited by the commands that change the list, which is what makes the
     * save automatic and keeps it worded once. {@link Storage#save} throws
     * rather than printing, because it has no business talking to the user;
     * catching the {@link IOException} here means a save failure costs the user
     * a warning rather than the session.
     * <p>
     * The warning already ends in a newline, so a caller can put it in front of
     * its own message unconditionally: a successful save contributes nothing and
     * a failed one leaves its warning on the line above.
     *
     * @param tasks The tasks to write.
     * @param ui Used to word a failed save.
     * @param storage Where to write them.
     * @return The empty string if the tasks were written, or the warning to
     *         show the user if they were not.
     */
    protected String save(TaskList tasks, Ui ui, Storage storage) {
        try {
            storage.save(tasks);
            return "";
        } catch (IOException e) {
            return ui.getSavingErrorMessage(e.getMessage()) + "\n";
        }
    }
}
