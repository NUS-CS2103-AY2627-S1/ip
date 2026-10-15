package larry.command;

import larry.exception.LarryException;
import larry.storage.Storage;
import larry.task.Task;
import larry.task.TaskList;
import larry.ui.Ui;

/**
 * Marks a specified task as not done.
 */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that unmarks the task at the specified index.
     *
     * @param taskIndex Zero-based index of the task to unmark.
     */
    public UnmarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LarryException {
        validateTaskIndex(tasks, taskIndex);
        Task task = tasks.get(taskIndex);
        task.markAsNotDone();
        saveTasks(tasks, ui, storage);
        ui.showTaskUnmarked(task);
    }
}
