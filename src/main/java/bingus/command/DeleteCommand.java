package bingus.command;

import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.Task;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Deletes one task from the task list and persists the updated list.
 */
public class DeleteCommand extends Command {
    private final int taskId;

    /**
     * Creates a command that deletes the specified task.
     *
     * @param taskId One-based number of the task to delete.
     */
    public DeleteCommand(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public CommandType getType() {
        return CommandType.DELETE;
    }

    /**
     * Removes the task, saves the list, and returns the result message.
     *
     * @param tasks task list to update.
     * @param ui user interface used to format the result.
     * @param storage persistent task storage.
     * @return task-deleted message.
     * @throws BingusException if the updated list cannot be saved.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws BingusException {
        assert taskId >= 1 && taskId <= tasks.size()
                : "Delete task ID must refer to an existing task";
        int taskIndex = taskId - 1;
        Task deletedTask = tasks.remove(taskIndex);

        try {
            storage.saveTasks(tasks.getTasksSnapshot());
        } catch (BingusException e) {
            tasks.add(taskIndex, deletedTask);
            throw e;
        }
        return ui.getDeleteTaskMessage(deletedTask, tasks.size());
    }
}
