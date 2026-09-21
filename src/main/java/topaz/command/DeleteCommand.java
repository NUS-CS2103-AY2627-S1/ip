package topaz.command;

import topaz.TopazException;
import topaz.storage.Storage;
import topaz.task.Task;
import topaz.task.TaskList;
import topaz.ui.Ui;

/**
 * Removes one task from the list and saves the updated task list.
 */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command for the given zero-based task index.
     *
     * @param taskIndex the zero-based index of the task to delete
     */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TopazException {
        // Parser validates taskIndex against the current task list before creating this command.
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "A delete command must refer to an existing task.";
        Task task = tasks.remove(taskIndex);
        try {
            storage.save(tasks.asList());
        } catch (TopazException exception) {
            tasks.add(taskIndex, task);
            assert tasks.get(taskIndex) == task
                    : "A failed save must restore the deleted task at its original position.";
            throw exception;
        }
        ui.showDeletedTask(task, tasks.size());
    }
}
