package xiaozhi.command;

import xiaozhi.exception.XiaoZhiException;
import xiaozhi.storage.Storage;
import xiaozhi.task.TaskList;
import xiaozhi.ui.Ui;

/**
 * Reverses the most recently executed undoable command (currently: adding,
 * deleting, marking or unmarking a task).
 * <p>
 * Repeating {@code undo} keeps walking further back through the session's
 * history, one command at a time; there is no "redo" for an undo that was
 * itself a mistake.
 */
public class UndoCommand extends Command {

    /**
     * Pops the most recently executed undoable command off {@code history}
     * and reverses its effect on {@code tasks}.
     *
     * @throws XiaoZhiException If there is no undoable command left to undo.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage, CommandHistory history) throws XiaoZhiException {
        if (history.isEmpty()) {
            throw new XiaoZhiException("There is nothing left to undo; the past cannot be unmade twice.");
        }
        Command lastCommand = history.pop();
        lastCommand.undo(tasks, ui, storage);
    }
}
