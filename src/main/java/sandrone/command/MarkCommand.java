package sandrone.command;

import sandrone.SandroneException;
import sandrone.storage.Storage;
import sandrone.task.Task;
import sandrone.task.TaskList;
import sandrone.ui.Ui;

/**
 * Marks a numbered task as complete.
 */
public class MarkCommand extends Command {
    private final int taskNumber;
    /**
     * Creates a command that marks the specified one-based task number.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the task as complete, saves it, and shows confirmation.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws SandroneException {
        Task task = tasks.markTaskByNumber(taskNumber);
        storage.save(tasks.getTasks());
        ui.showTaskMarked(task, true);
    }
}
