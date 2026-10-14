package bingus.command;

import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.Task;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Marks or unmarks a task.
 */
public class MarkCommand extends Command {

    private final int taskId;
    private final boolean isToMark;

    /**
     * Creates a command that changes a task's completion status.
     *
     * @param taskId One-based number of the task to update.
     * @param isToMark Whether the task should be marked complete.
     */
    public MarkCommand(int taskId, boolean isToMark) {
        this.taskId = taskId;
        this.isToMark = isToMark;
    }

    @Override
    public CommandType getType() {
        return CommandType.MARK;
    }

    /**
     * Updates the task's completion status, saves the list, and returns the result message.
     *
     * @param tasks task list containing the task to update.
     * @param ui user interface used to format the result.
     * @param storage persistent task storage.
     * @return task-marked or task-unmarked message.
     * @throws BingusException if the updated list cannot be saved.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws BingusException {
        assert taskId >= 1 && taskId <= tasks.size()
                : "Mark task ID must refer to an existing task";
        Task task = tasks.get(taskId - 1);
        boolean wasDone = task.isDone();
        if (isToMark) {
            task.mark();
        } else {
            task.unmark();
        }

        try {
            storage.saveTasks(tasks.getTasksSnapshot());
        } catch (BingusException e) {
            if (wasDone) {
                task.mark();
            } else {
                task.unmark();
            }
            throw e;
        }

        if (isToMark) {
            return ui.getTaskMarkedMessage(task);
        } else {
            return ui.getTaskUnmarkedMessage(task);
        }
    }
}
