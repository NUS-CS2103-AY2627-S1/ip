package crystal.command;

import java.util.ArrayList;
import java.util.List;

import crystal.exception.CrystalException;
import crystal.storage.Storage;
import crystal.task.Task;
import crystal.task.TaskList;
import crystal.ui.Ui;

/**
 * Deletes one task and persists the shortened task list.
 */
public final class DeleteCommand extends TaskIndexCommand {
    /**
     * Creates a delete command for a zero-based task index.
     *
     * @param taskIndex zero-based task index.
     */
    public DeleteCommand(int taskIndex) {
        super(taskIndex);
    }

    /**
     * Deletes, saves, and displays the target task.
     *
     * @param tasks task list containing the target.
     * @param ui console interface used to present the result.
     * @param storage persistence service for the shortened task list.
     * @throws CrystalException if the task does not exist or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws CrystalException {
        Task task = getTask(tasks);
        List<Task> updatedTasks = new ArrayList<>(tasks.getTasks());
        updatedTasks.remove(taskIndex);
        storage.saveTasks(updatedTasks);
        tasks.deleteTask(taskIndex);
        ui.showTaskDeleted(task, tasks.getTaskCount());
    }
}
