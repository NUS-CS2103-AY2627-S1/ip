package will.command;

import will.Storage;
import will.TaskList;
import will.Ui;
import will.WillException;

/**
 * A single user command, parsed out of raw input by {@link Parser}.
 * Each concrete subclass knows how to carry out exactly one kind of
 * command against the task list.
 */
public abstract class Command {
    /** Carries out this command against the given tasks/ui/storage. */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws WillException;

    /** Whether this command should end the program's main loop. */
    public boolean isExit() {
        return false;
    }

    /**
     * Shared bounds check for mark/unmark/delete, which all take a
     * task-number argument that Parser can't validate against the
     * current list size (Parser only sees the raw command text, not
     * the TaskList) — so each of those commands validates it here,
     * right at the start of execute(), before touching the list.
     */
    protected static void requireValidIndex(TaskList tasks, int index) throws WillException {
        if (index < 0 || index >= tasks.size()) {
            throw new WillException("Task number " + (index + 1) + " doesn't exist. "
                    + "You have " + tasks.size() + " task(s) in your list.");
        }
        // Documents, right where callers rely on it, that this method's
        // whole job is to guarantee this: anything past this point is
        // free to assume the index is in range without re-checking.
        assert index >= 0 && index < tasks.size() : "index should be in range once no exception was thrown";
    }
}
