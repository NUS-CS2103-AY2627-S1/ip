package sandrone.command;

import sandrone.SandroneException;
import sandrone.storage.Storage;
import sandrone.task.Task;
import sandrone.task.TaskList;
import sandrone.ui.Ui;

/**
 * Removes a numbered task.
 */
public class RemoveCommand extends Command {
    private final int taskNumber;
    /**
     * Creates a command that removes the specified one-based task number.
     */
    public RemoveCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Removes the task, displays it, and saves the updated list.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws SandroneException {
        Task task = tasks.removeTaskByNumber(taskNumber);
        ui.showTaskRemoved(task);
        storage.save(tasks.getTasks());
    }
}
