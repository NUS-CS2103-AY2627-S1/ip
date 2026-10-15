package xiaozhi.command;

import xiaozhi.exception.XiaoZhiException;
import xiaozhi.storage.Storage;
import xiaozhi.task.TaskList;
import xiaozhi.ui.Ui;

/**
 * Represents an action XiaoZhi can carry out in response to a user command.
 * <p>
 * A Command is built by {@link xiaozhi.parser.Parser} from raw input text,
 * then run against the current task list, UI and storage.
 */
public abstract class Command {

    /**
     * Carries out this command's effect on the given task list, reporting
     * to the user through {@code ui} and persisting any change through
     * {@code storage}.
     *
     * @param tasks The current task list.
     * @param ui The UI to report results or errors through.
     * @param storage The storage to persist any change to the task list to.
     * @param history Record of undoable commands executed so far; commands that mutate
     *         {@code tasks} in a reversible way should not push themselves here directly
     *         -- {@link xiaozhi.XiaoZhi} does that once {@link #execute} returns
     *         successfully, based on {@link #isUndoable()}. Only {@link UndoCommand}
     *         reads from it.
     * @throws XiaoZhiException If the command cannot be carried out, e.g. an invalid task number.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage, CommandHistory history)
            throws XiaoZhiException;

    /**
     * Returns whether this command should end XiaoZhi's main loop.
     * Defaults to {@code false}; only {@link ExitCommand} overrides this.
     *
     * @return {@code true} if XiaoZhi should exit after this command, {@code false} otherwise.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Returns whether this command's effect on the task list can be reversed
     * by {@link UndoCommand}. Defaults to {@code false}; commands that mutate
     * the task list (add/delete/mark/unmark) override this to {@code true} and
     * also override {@link #undo}.
     *
     * @return {@code true} if this command supports {@link #undo}, {@code false} otherwise.
     */
    public boolean isUndoable() {
        return false;
    }

    /**
     * Reverses the effect this command had when it was executed, reporting the
     * reversal through {@code ui} and persisting the result through {@code storage}.
     * <p>
     * Only meaningful for a command whose {@link #isUndoable()} returns
     * {@code true}; the default implementation always throws, since a
     * non-undoable command is never pushed onto a {@link CommandHistory} in
     * the first place.
     *
     * @param tasks The current task list.
     * @param ui The UI to report the reversal through.
     * @param storage The storage to persist the reversed task list to.
     * @throws XiaoZhiException If this command does not support undo.
     */
    public void undo(TaskList tasks, Ui ui, Storage storage) throws XiaoZhiException {
        throw new XiaoZhiException("This command cannot be undone.");
    }
}
