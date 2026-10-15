package dukey.command;

import dukey.exception.DukeyException;
import dukey.storage.Storage;
import dukey.task.Task;
import dukey.task.TaskList;
import dukey.ui.Ui;

/**
 * Marks a task as not done.
 */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeyException {
        if (!tasks.isValidTaskNumber(this.taskNumber)) {
            throw new DukeyException("Please provide a valid task number.");
        }

        Task task = tasks.get(this.taskNumber);
        task.markAsUndone();
        storage.save(tasks);
        ui.showTaskUnmarked(task);
    }

    @Override
    public String getResponseStyleClass() {
        return STYLE_STATUS;
    }
}
