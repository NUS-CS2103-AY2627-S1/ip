package sandrone.command;

import sandrone.SandroneException;
import sandrone.storage.Storage;
import sandrone.task.Task;
import sandrone.task.TaskList;
import sandrone.ui.Ui;

/**
 * Marks a numbered task as incomplete.
 */
public class UnmarkCommand extends Command {
    private final int taskNumber;
    /**
     * Creates a command that unmarks the specified one-based task number.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the task as incomplete, saves it, and shows confirmation.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws SandroneException {
        Task task = tasks.unmarkTaskByNumber(taskNumber);
        storage.save(tasks.getTasks());
        ui.showTaskMarked(task, false);
    }
}
