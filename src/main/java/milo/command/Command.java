package milo.command;

import milo.exception.MiloException;
import milo.storage.Storage;
import milo.task.TaskList;
import milo.ui.Ui;

/**
 * Represents one user command that {@link Parser} has already fully
 * interpreted: what kind of command it is, and any data it needs. Carrying
 * it out (reading or changing the task list, talking to the user, saving to
 * disk) happens in {@link #execute}.
 */
public abstract class Command {
    /**
     * Carries out this command.
     *
     * @param tasks   the current task list, which this command may read or change
     * @param ui      how this command reports back to the user
     * @param storage where this command saves the task list, if it changes it
     * @throws MiloException if the command cannot be carried out, e.g. an
     *                       out-of-range task number
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws MiloException;

    /**
     * Returns whether this command ends the session.
     * Overridden only by {@link ExitCommand}; every other command lets the
     * session continue after it runs.
     *
     * @return true if Milo should stop reading further commands after this one
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Returns whether this command changes the task list, and so needs the
     * list snapshotted before it runs so that "undo" can put it back.
     * Overridden by the commands that add, delete, mark and unmark; the rest
     * only read the list, and leave the existing snapshot alone so that undo
     * keeps referring to the last command that actually changed something.
     *
     * @return true if this command changes the task list
     */
    public boolean isUndoable() {
        return false;
    }

    /**
     * Works out which task in the list a number the user typed refers to.
     * Shared by every command that acts on an existing task (mark, unmark,
     * delete), since they all need the same checks: is there anything to act
     * on, is the text actually a number, and is it in range. The range check
     * needs the list's current size, which is only known once execution
     * starts, so this lives here rather than in {@link Parser}.
     *
     * @param tasks        the current task list
     * @param verb         the command word used, quoted back in any error
     * @param rawArguments the task number the user typed, as text
     * @return the position of that task in the list, counting from 0
     * @throws MiloException if the list is empty, or no valid task number was given
     */
    protected static int resolveTaskIndex(TaskList tasks, String verb, String rawArguments)
            throws MiloException {
        if (tasks.isEmpty()) {
            throw new MiloException("Nothing on your list to " + verb + " yet. "
                    + "Give me something first, like: todo borrow book");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(rawArguments.trim());
        } catch (NumberFormatException e) {
            // Covers a missing number as well as a non-numeric one. The
            // original exception is not useful to the user, so it is replaced
            // with an explanation phrased in terms of the command they typed.
            throw new MiloException("Which one should I " + verb + "? "
                    + "Point me at a number, like: " + verb + " 2");
        }

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new MiloException("There's no task " + taskNumber + " on your list. "
                    + "Pick a number between 1 and " + tasks.size() + ".");
        }

        // The user counts from 1, so 1 maps to index 0.
        int index = taskNumber - 1;
        // Guaranteed by the range check above; stated here because every
        // caller feeds this straight into TaskList without re-checking.
        assert index >= 0 && index < tasks.size() : "resolved index should be inside the list";
        return index;
    }
}
