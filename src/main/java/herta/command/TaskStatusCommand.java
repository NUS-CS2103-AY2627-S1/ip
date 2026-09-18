package herta.command;

import herta.exception.HertaException;
import herta.storage.Storage;
import herta.task.Task;
import herta.task.TaskList;
import herta.ui.UiOutput;

/**
 * Base class for commands that change a selected task's completion status.
 */
public abstract class TaskStatusCommand extends TaskIndexCommand {

    /**
     * Creates a command for a zero-based task index.
     *
     * @param taskIndex the zero-based index of the selected task.
     */
    protected TaskStatusCommand(int taskIndex) {
        super(taskIndex);
    }

    /**
     * Updates and persists the selected task's completion status when needed.
     * Reports a no-op without mutating or saving the task when its status already matches.
     *
     * @param tasks the task list to update.
     * @param ui the output interface used to display the result.
     * @param storage the storage used to persist the status change.
     * @param shouldBeCompleted the completion status to apply.
     * @param successMessage the message shown after a real status change.
     * @param noOpMessage the message shown when the requested status is already set.
     * @throws HertaException if the selected task is invalid or cannot be saved.
     */
    protected void executeStatusChange(TaskList tasks, UiOutput ui, Storage storage,
                                       boolean shouldBeCompleted, String successMessage,
                                       String noOpMessage)
            throws HertaException {
        int taskIndex = getTaskIndex(tasks);
        Task task = tasks.get(taskIndex);
        if (task.isCompleted() == shouldBeCompleted) {
            ui.showMessage(noOpMessage);
            return;
        }

        boolean wasCompleted = task.isCompleted();
        if (shouldBeCompleted) {
            tasks.markTask(taskIndex);
        } else {
            tasks.unmarkTask(taskIndex);
        }
        try {
            storage.save(tasks);
        } catch (HertaException | RuntimeException e) {
            tasks.restoreStatus(taskIndex, wasCompleted);
            throw e;
        }
        ui.showMessage(successMessage);
        ui.showTask(task);
    }
}
