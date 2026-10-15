package clearblue.command;

import clearblue.ClearblueException;
import clearblue.storage.Storage;
import clearblue.task.TaskList;
import clearblue.ui.Ui;

/**
 * Reverses the most recently executed undoable command. {@link
 * clearblue.parser.Parser} has no access to command history when it
 * creates this command, so {@link clearblue.Clearblue} injects the target
 * via {@link #setCommandToUndo} right after parsing, before executing it.
 */
public class UndoCommand extends Command {
    private Command commandToUndo;

    /**
     * Sets the command this undo should reverse.
     *
     * @param commandToUndo the most recently executed undoable command,
     *     or {@code null} if there is none
     */
    public void setCommandToUndo(Command commandToUndo) {
        this.commandToUndo = commandToUndo;
    }

    /**
     * Reverses {@code commandToUndo}'s effect.
     *
     * @param tasks task list to revert
     * @param ui user interface to report through
     * @param storage storage to persist the reverted list through
     * @throws ClearblueException if there is nothing to undo, or saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ClearblueException {
        if (commandToUndo == null) {
            throw new ClearblueException("There is nothing to undo.");
        }
        commandToUndo.undo(tasks, ui, storage);
    }
}
