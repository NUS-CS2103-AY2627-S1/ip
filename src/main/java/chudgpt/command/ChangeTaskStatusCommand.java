package chudgpt.command;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/** Changes the completion status of one task. */
public class ChangeTaskStatusCommand extends Command {
    private final int taskIndex;
    private final boolean isCompleted;

    /**
     * Creates a command that changes a task to the specified completion status.
     *
     * @param taskIndex zero-based index of the task to update.
     * @param isCompleted target completion status.
     */
    public ChangeTaskStatusCommand(int taskIndex, boolean isCompleted) {
        this.taskIndex = taskIndex;
        this.isCompleted = isCompleted;
    }

    /**
     * Updates the task status, saves the task list, and returns a confirmation message.
     *
     * @param tasks the current task list
     * @param ui the UI handler used to create the confirmation
     * @param storage the storage handler used to save the updated list
     * @return the response confirming the task's new status
     * @throws ChudException if the task index is invalid
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        Task updatedTask = tasks.updateTask(taskIndex, isCompleted);
        storage.save(tasks);
        return ui.getTaskStatusMessage(updatedTask);
    }
}
