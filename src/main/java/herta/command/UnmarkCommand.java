package herta.command;

import herta.exception.HertaException;
import herta.storage.Storage;
import herta.task.TaskList;
import herta.ui.UiOutput;

/**
 * Represents the command that marks a task as incomplete.
 */
public class UnmarkCommand extends TaskStatusCommand {
    private static final String UNMARK_CONFIRMATION_MESSAGE = "Fine. It's incomplete again:";
    private static final String UNMARK_NO_OP_MESSAGE = "Already incomplete. There is nothing to undo.";

    /**
     * Creates a command that marks the task at the given index as incomplete.
     *
     * @param taskIndex the zero-based index of the task to unmark.
     */
    public UnmarkCommand(int taskIndex) {
        super(taskIndex);
    }

    /**
     * Unmarks the selected task when needed and reports the result.
     * The in-memory status is restored if saving a real change fails.
     *
     * @param tasks the task list to update.
     * @param ui the output interface used to display responses.
     * @param storage the storage used to persist the status change.
     * @throws HertaException if the selected task is invalid or cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, UiOutput ui, Storage storage) throws HertaException {
        executeStatusChange(tasks, ui, storage, false,
                UNMARK_CONFIRMATION_MESSAGE, UNMARK_NO_OP_MESSAGE);
    }
}
