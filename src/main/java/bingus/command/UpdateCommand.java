package bingus.command;

import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.Task;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Replaces one task with an updated version and persists the modified list.
 */
public class UpdateCommand extends Command {

    private final int taskId;
    private final Task updatedTask;

    /**
     * Creates a command that replaces the specified task.
     *
     * @param taskId one-based number of the task to replace.
     * @param updatedTask replacement containing the updated task details.
     */
    public UpdateCommand(int taskId, Task updatedTask) {
        assert updatedTask != null : "Updated task must not be null";

        this.taskId = taskId;
        this.updatedTask = updatedTask;
    }

    @Override
    public CommandType getType() {
        return CommandType.UPDATE;
    }

    /**
     * Replaces the task, saves the list, and returns the result message.
     *
     * @param tasks task list to update.
     * @param ui user interface used to format the result.
     * @param storage persistent task storage.
     * @return task-updated message.
     * @throws BingusException if the updated list cannot be saved.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws BingusException {
        assert taskId >= 1 && taskId <= tasks.size()
                : "Update task ID must refer to an existing task";

        int taskIndex = taskId - 1;
        Task originalTask = tasks.get(taskIndex);
        if (originalTask.isDone()) {
            updatedTask.mark();
        } else {
            updatedTask.unmark();
        }
        tasks.replace(taskIndex, updatedTask);

        try {
            storage.saveTasks(tasks.getTasksSnapshot());
        } catch (BingusException e) {
            tasks.replace(taskIndex, originalTask);
            throw e;
        }
        return ui.getTaskUpdatedMessage(updatedTask);
    }
}
