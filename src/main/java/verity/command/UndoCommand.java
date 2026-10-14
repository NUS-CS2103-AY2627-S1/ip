package verity.command;

import verity.storage.Storage;
import verity.task.TaskList;

/**
 * Reverses the undoable command at the top of the session's history, if any.
 */
public class UndoCommand extends Command {
    private final Undoable lastUndoableCommand;

    /**
     * Creates a command that reverses the action currently at the top of the undo history.
     *
     * @param lastUndoableCommand Top undoable command in the current session, or {@code null}
     *                            if there is nothing to undo.
     */
    public UndoCommand(Undoable lastUndoableCommand) {
        this.lastUndoableCommand = lastUndoableCommand;
    }

    /**
     * Reverses {@link #lastUndoableCommand}, or reports that there's nothing to undo.
     *
     * @param tasks {@inheritDoc}
     * @param storage {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String execute(TaskList tasks, Storage storage) {
        if (lastUndoableCommand == null) {
            return "There's nothing to undo, genius.";
        }
        return lastUndoableCommand.undo(tasks, storage);
    }
}
