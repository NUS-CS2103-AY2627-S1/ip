package dukey.command;

import dukey.exception.DukeyException;
import dukey.storage.Storage;
import dukey.task.Task;
import dukey.task.TaskList;
import dukey.ui.Ui;

/**
 * Deletes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeyException {
        if (!tasks.isValidTaskNumber(this.taskNumber)) {
            throw new DukeyException("Please provide a valid task number.");
        }

        Task task = tasks.delete(this.taskNumber);
        storage.save(tasks);
        ui.showTaskDeleted(task, tasks);
    }

    @Override
    public String getResponseStyleClass() {
        return STYLE_DELETE;
    }
}
