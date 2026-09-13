package alfred.command;

import alfred.exception.AlfredException;
import alfred.storage.Storage;
import alfred.task.Task;
import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * Removes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that removes the task at the given zero-based index.
     *
     * @param taskIndex zero-based index of the task to remove
     */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws AlfredException {
        Task deletedTask = tasks.remove(taskIndex);
        try {
            storage.save(tasks);
        } catch (AlfredException e) {
            tasks.add(taskIndex, deletedTask);
            throw e;
        }
        ui.showTaskDeleted(deletedTask, tasks.size());
    }
}
