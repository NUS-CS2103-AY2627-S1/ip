package herta.command;

import herta.exception.HertaException;
import herta.storage.Storage;
import herta.task.TaskList;
import herta.ui.UiOutput;

/**
 * Represents the command that marks a task as complete.
 */
public class MarkCommand extends TaskStatusCommand {
    private static final String MARK_CONFIRMATION_MESSAGE = "Done. It's marked complete:";
    private static final String MARK_NO_OP_MESSAGE = "Already complete. There is nothing more to do.";

    /**
     * Creates a command that marks the task at the given index.
     *
     * @param taskIndex the zero-based index of the task to mark.
     */
    public MarkCommand(int taskIndex) {
        super(taskIndex);
    }

    /**
     * Marks the selected task when needed and reports the result.
     * The in-memory status is restored if saving a real change fails.
     *
     * @param tasks the task list to update.
     * @param ui the output interface used to display responses.
     * @param storage the storage used to persist the status change.
     * @throws HertaException if the selected task is invalid or cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, UiOutput ui, Storage storage) throws HertaException {
        executeStatusChange(tasks, ui, storage, true,
                MARK_CONFIRMATION_MESSAGE, MARK_NO_OP_MESSAGE);
    }
}
