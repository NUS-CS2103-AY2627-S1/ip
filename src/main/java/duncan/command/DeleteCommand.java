package duncan.command;

import duncan.DuncanException;
import duncan.Storage;
import duncan.Ui;
import duncan.task.Task;
import duncan.task.TaskList;

/** Removes the task at a given 0-based index from the task list. */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that removes the task at the given index.
     *
     * @param taskIndex The 0-based index of the task to remove.
     */
    public DeleteCommand(int taskIndex) {
        assert taskIndex >= 0;
        this.taskIndex = taskIndex;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DuncanException {
        checkTaskIndex(tasks, taskIndex);
        Task removedTask = tasks.remove(taskIndex);
        ui.showTaskDeleted(removedTask, tasks.size());
        saveTasks(tasks, ui, storage);
    }
}
