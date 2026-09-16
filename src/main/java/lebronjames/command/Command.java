package lebronjames.command;

import lebronjames.LebronJamesException;
import lebronjames.storage.Storage;
import lebronjames.task.TaskList;
import lebronjames.ui.Ui;

/**
 * Represents one instruction the user has given, ready to be carried out.
 *
 * <p>Each kind of command is a subclass that knows how to do its own job, so
 * the run loop no longer needs a long if-else chain asking "which command is
 * this?". It simply calls {@link #execute} and lets the object answer.
 *
 * <p>A command is created by {@link lebronjames.parser.Parser Parser} once the user's input has been
 * understood, and is given everything it needs at execution time rather than at
 * construction time. That keeps a command a plain description of an intention:
 * "delete task 3" can be created and inspected without any task list existing.
 */
public abstract class Command {
    /**
     * Carries out this command.
     *
     * @param tasks Task list to read or change.
     * @param ui User interface used to report the result.
     * @param storage Storage used to save the list if this command changed it.
     * @throws LebronJamesException If the command cannot be carried out, for
     *     example because it names a task that does not exist.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws LebronJamesException;

    /**
     * Returns whether the chatbot should stop after this command.
     *
     * <p>Only {@link ExitCommand} overrides this, so the loop asks the command
     * itself instead of comparing the input against "bye" a second time.
     *
     * @return Whether this command ends the program.
     */
    public boolean isExit() {
        return false;
    }
}
