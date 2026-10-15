package bob.command;

import java.io.IOException;

import bob.BobException;
import bob.Storage;
import bob.Ui;
import bob.task.Task;
import bob.task.TaskList;

/**
 * Represents a request to mark one task as complete.
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command for a one-based task number.
     *
     * @param taskNumber number of the task to mark
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks and saves the selected task, restoring its state if saving fails.
     *
     * @param tasks task list to update
     * @param ui user interface used for confirmation
     * @param storage storage used to persist the updated list
     * @throws BobException if the task number is outside the list
     * @throws IOException if the updated task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BobException, IOException {
        int taskIndex = tasks.getTaskIndex(taskNumber);
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        tasks.mark(taskIndex);
        try {
            storage.save(tasks.asList());
        } catch (IOException exception) {
            tasks.restoreDoneState(taskIndex, wasDone);
            throw exception;
        }
        ui.showMarked(task);
    }
}
