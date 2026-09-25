package shan.command;

import shan.exception.DataFileException;
import shan.exception.InvalidArgumentException;
import shan.storage.Storage;
import shan.task.TaskList;

/**
 * Restores the task list to its state before the most recent mutating command.
 */
public class UndoCommand extends Command {
    /**
     * Constructs an undo command.
     */
    public UndoCommand() {
    }

    /**
     * Restores and persists the most recent undo state.
     *
     * @param tasks   Task list to restore.
     * @param storage Storage used to persist the restored task list.
     * @return Confirmation that the previous command was undone.
     * @throws InvalidArgumentException If no command is available to undo.
     * @throws DataFileException        If the restored task list cannot be saved.
     */
    @Override
    public String execute(TaskList tasks, Storage storage)
            throws InvalidArgumentException, DataFileException {
        if (!tasks.canUndo()) {
            throw new InvalidArgumentException("There is no command to undo.");
        }

        TaskList.State stateBeforeUndo = tasks.createState();
        tasks.restoreUndoState();
        try {
            storage.save(tasks.snapshot());
        } catch (DataFileException exception) {
            tasks.restoreState(stateBeforeUndo);
            throw exception;
        }
        tasks.clearUndoState();
        return "Got it. I've undone your last command.";
    }
}
