package chudgpt.command;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/** Deletes one task from the current task list. */
public class DeleteTaskCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that deletes the task at the specified index.
     *
     * @param taskIndex zero-based index of the task to delete.
     */
    public DeleteTaskCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Deletes the selected task, saves the task list, and returns a confirmation message.
     *
     * @param tasks the current task list
     * @param ui the UI handler used to create the confirmation
     * @param storage the storage handler used to save the updated list
     * @return the response confirming that the task was deleted
     * @throws ChudException if the task index is invalid
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        Task deletedTask = tasks.deleteTask(taskIndex);
        storage.save(tasks);
        return ui.getTaskDeletedMessage(deletedTask, tasks.size());
    }
}
