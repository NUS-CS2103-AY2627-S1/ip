package arrodes.command;

import arrodes.exception.ArrodesException;
import arrodes.storage.Storage;
import arrodes.task.Task;
import arrodes.task.TaskList;
import arrodes.ui.Ui;

/** Command that removes a numbered task and saves the updated list. */
public class DeleteCommand extends Command {
    /** One-based number of the task to remove. */
    private final int taskNumber;

    /**
     * Creates a command for deleting a task.
     * @param taskNumber one-based task number
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes the task, persists the list, and reports the removed task.
     * @param ui interface used for output
     * @param taskList list to update
     * @param storage persistence layer
     */
    @Override
    public void execute(Ui ui, TaskList taskList, Storage storage) {
        Task task = taskList.getTaskByNumber(taskNumber);
        taskList.delete(taskNumber);
        try {
            storage.save(taskList);
        } catch (ArrodesException exception) {
            taskList.restore(taskNumber - 1, task);
            throw exception;
        }
        ui.showMessage("The following record has been erased from the annals:\n"
                + task + "\n"
                + taskList.getSize()
                + " tasks remaining are being tracked");
    }
}
