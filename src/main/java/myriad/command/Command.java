package myriad.command;

import java.io.IOException;

import myriad.MyriadException;
import myriad.storage.Storage;
import myriad.task.TaskList;
import myriad.ui.Ui;

/**
 * One command the user asked for, ready to be carried out. The
 * {@code Parser} turns a line of input into the matching {@code Command}
 * subclass, and {@code Myriad} just calls {@link #execute} on whatever it
 * gets back. {@code Myriad} therefore never needs to know the list of
 * commands, and adding a command means adding a subclass here rather than
 * editing a switch in {@code Myriad}.
 *
 * Each subclass holds whatever its own arguments were (a task to add, a
 * task number, a date to search for), so by the time {@link #execute}
 * runs there is no input text left to interpret.
 */
public abstract class Command {

    /**
     * Creates a command. Nothing to set up here — each subclass holds its
     * own arguments, if it has any.
     */
    protected Command() {
    }

    /**
     * Carries out this command against the given task list, reporting
     * whatever the user should see through {@code ui} and persisting any
     * change through {@code storage}. Throws {@link MyriadException} if the
     * command can't be carried out (e.g. it names a task that doesn't
     * exist); {@code Myriad} catches that and shows it as an error.
     *
     * @param tasks   the session's task list, read and possibly changed.
     * @param ui      what to report the outcome through.
     * @param storage where to persist the list if this command changes it.
     * @throws MyriadException if the command can't be carried out.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws MyriadException;

    /**
     * Returns whether the session should end after this command. False for
     * every command except {@link ExitCommand}, which is why the default is
     * here rather than repeated in each subclass.
     *
     * @return true only if the command loop should stop after this command.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves the task list's current state, wrapping any {@link IOException}
     * (disk full, permission denied, etc.) into a {@link MyriadException} so
     * it's shown like any other command error instead of crashing the
     * program. Provided here, rather than in each subclass, so every
     * mutating command ({@code add}, {@code mark}, {@code unmark},
     * {@code delete}) translates a save failure into the same user-facing
     * message.
     *
     * @param tasks   the list to write out in full.
     * @param storage where to write it.
     * @throws MyriadException if the write fails, wrapping the
     *                         {@code IOException}'s message.
     */
    protected void save(TaskList tasks, Storage storage) throws MyriadException {
        try {
            storage.save(tasks);
        } catch (IOException e) {
            throw new MyriadException(
                    "Couldn't save your tasks to disk (the list is still correct for this "
                            + "session, but changes won't be there next time you start): " + e.getMessage());
        }
    }
}
