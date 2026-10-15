package beemo.command;

import beemo.BeemoException;
import beemo.storage.Storage;
import beemo.task.Task;
import beemo.task.TaskList;
import beemo.ui.Ui;

/**
 * Deletes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command targeting the task with the specified display number.
     *
     * @param taskNumber Display number of the task to delete.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BeemoException {
        Task removedTask = tasks.delete(taskNumber);
        storage.saveTasks(tasks.asList());
        ui.showDeletedTask(removedTask, tasks.size());
    }
}
