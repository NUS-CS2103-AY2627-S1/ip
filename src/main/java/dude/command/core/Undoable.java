package dude.command.core;

import dude.exception.CommandExecutionException;

/**
 * Describes a command whose state change can be reversed and reapplied.
 */
public interface Undoable {
    /**
     * Returns whether this command completed a reversible state change.
     *
     * @return True when the command should be stored by the invoker.
     */
    boolean isAvailableForUndo();

    /**
     * Reverses the command's state change.
     *
     * @throws CommandExecutionException If the restored state cannot be persisted.
     */
    void undo() throws CommandExecutionException;

    /**
     * Reapplies the command's state change.
     *
     * @throws CommandExecutionException If the reapplied state cannot be persisted.
     */
    void redo() throws CommandExecutionException;

    /**
     * Returns the user-facing description of the reversed command.
     *
     * @return Undo description.
     */
    String undoDescription();
}
