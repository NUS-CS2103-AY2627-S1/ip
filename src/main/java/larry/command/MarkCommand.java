package larry.command;

import larry.exception.LarryException;
import larry.storage.Storage;
import larry.task.Task;
import larry.task.TaskList;
import larry.ui.Ui;

/**
 * Marks a specified task as done.
 */
public class MarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that marks the task at the specified index.
     *
     * @param taskIndex Zero-based index of the task to mark.
     */
    public MarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LarryException {
        validateTaskIndex(tasks, taskIndex);
        Task task = tasks.get(taskIndex);
        task.markAsDone();
        saveTasks(tasks, ui, storage);
        ui.showTaskMarked(task);
    }
}
