package summer.command;

import summer.storage.Storage;
import summer.task.Task;
import summer.task.TaskList;
import summer.ui.Ui;

/** The "unmark INDEX" command: marks a task as not done. */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates the command.
     *
     * @param taskIndex zero-based index of the task to mark as not done
     */
    public UnmarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /** Marks the task at the stored index as not done, or reports it if the index is invalid. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (!tasks.hasTaskAt(this.taskIndex)) {
            ui.showTaskNotFound();
            return;
        }
        Task task = tasks.get(this.taskIndex);
        task.markNotDone();
        storage.save(tasks);
        ui.showTaskUnmarked(task);
    }
}
