package summer.command;

import summer.storage.Storage;
import summer.task.Task;
import summer.task.TaskList;
import summer.ui.Ui;

/** The "delete INDEX" command: removes a task from the list. */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /**
     * Creates the command.
     *
     * @param taskIndex zero-based index of the task to remove
     */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /** Removes the task at the stored index, or reports it if the index is invalid. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (!tasks.hasTaskAt(this.taskIndex)) {
            ui.showTaskNotFound();
            return;
        }
        Task task = tasks.delete(this.taskIndex);
        storage.save(tasks);
        ui.showTaskDeleted(task, tasks.size());
    }
}
