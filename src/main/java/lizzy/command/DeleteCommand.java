package lizzy.command;

import lizzy.exception.LizzyException;
import lizzy.storage.Storage;
import lizzy.task.Task;
import lizzy.task.TaskList;
import lizzy.ui.Ui;

/**
 * Deletes one numbered task from the task list.
 */
public class DeleteCommand extends Command {
    /**
     * The one-based task number to delete.
     */
    private final int taskNumber;

    /**
     * Creates a command that deletes the supplied task number.
     *
     * @param taskNumber the one-based number of the task to delete
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes, saves, and confirms the referenced task.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LizzyException {
        Task task = tasks.deleteTask(taskNumber, "delete");
        storage.save(tasks.asList());
        ui.showTaskDeleted(task, tasks.size());
    }
}
