package duncan.command;

import duncan.DuncanException;
import duncan.Storage;
import duncan.Ui;
import duncan.task.Task;
import duncan.task.TaskList;

/** Marks the task at a given 0-based index as not done, or says so if it already is not done. */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that marks the task at the given index as not done.
     *
     * @param taskIndex The 0-based index of the task to mark as not done.
     */
    public UnmarkCommand(int taskIndex) {
        assert taskIndex >= 0;
        this.taskIndex = taskIndex;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DuncanException {
        checkTaskIndex(tasks, taskIndex);
        Task task = tasks.get(taskIndex);
        if (!task.isDone()) {
            ui.showTaskAlreadyUnmarked(taskIndex + 1);
            return;
        }
        task.markAsNotDone();
        ui.showTaskUnmarked(task);
        saveTasks(tasks, ui, storage);
    }
}
