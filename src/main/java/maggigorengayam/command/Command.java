package maggigorengayam.command;

import java.io.IOException;

import maggigorengayam.MaggiGorengAyamException;
import maggigorengayam.storage.Storage;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/**
 * One user command, already understood by {@link maggigorengayam.parser.Parser}
 * and ready to run against the live task list. Each kind of command (add,
 * mark, delete, list, ...) is its own subclass, so MaggiGorengAyam's main
 * loop no longer needs to know how any particular command works - it just
 * calls {@link #execute}.
 */
public abstract class Command {
    /**
     * Carries out this command: mutating {@code tasks} if needed, saving
     * to {@code storage} if the mutation should persist, and returning
     * the result message (built via {@code ui}) for the caller to display.
     */
    public abstract String execute(TaskList tasks, Ui ui, Storage storage) throws MaggiGorengAyamException;

    /** Whether this command should end the program's main loop. Only ExitCommand overrides this. */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves the current task list to disk, instead of letting an
     * IOException crash the whole program. Shared by every command that
     * mutates the task list.
     *
     * @return true if the save succeeded, false if it failed - callers
     *         should return {@link Ui#showSaveError()} instead of their
     *         normal success message in that case.
     */
    protected static boolean saveTasks(Storage storage, TaskList tasks) {
        try {
            storage.save(tasks.getAll());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Converts a 1-indexed task number that {@link maggigorengayam.parser.Parser}
     * already confirmed is a well-formed whole number into a valid 0-based index
     * into the current task list, or reports it as out of range. This is
     * a range check against live list state, which is why it lives here
     * rather than in Parser (which never sees the TaskList). Shared by
     * MarkCommand/UnmarkCommand/DeleteCommand.
     */
    protected static int toValidIndex(int number, int taskCount) throws MaggiGorengAyamException {
        if (number < 1 || number > taskCount) {
            throw new MaggiGorengAyamException(
                    "No task number " + number + " lah, you only got " + taskCount + " task(s).");
        }
        int index = number - 1;
        // Postcondition this method promises to TaskList#get/remove, which assume
        // a pre-validated index and no longer range-check it themselves.
        assert index >= 0 && index < taskCount
                : "computed index " + index + " out of bounds for count " + taskCount;
        return index;
    }
}
