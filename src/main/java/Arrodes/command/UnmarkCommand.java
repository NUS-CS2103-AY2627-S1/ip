package arrodes.command;

import arrodes.exception.ArrodesException;
import arrodes.storage.Storage;
import arrodes.task.Task;
import arrodes.task.TaskList;
import arrodes.ui.Ui;

/** Command that marks a numbered task as incomplete. */
public class UnmarkCommand extends Command {
    /** One-based number of the task to reopen. */
    private final int taskNumber;

    /**
     * Creates a command for reopening a task.
     * @param taskNumber one-based task number
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the task incomplete, persists the list, and reports the result.
     * @param ui interface used for output
     * @param taskList list to update
     * @param storage persistence layer
     */
    @Override
    public void execute(Ui ui, TaskList taskList, Storage storage) {
        Task task = taskList.getTaskByNumber(taskNumber);
        boolean wasDone = task.isDone();
        task.markAsNotDone();
        try {
            storage.save(taskList);
        } catch (ArrodesException exception) {
            if (wasDone) {
                task.markAsDone();
            }
            throw exception;
        }
        ui.showMessage("By your decree, the mark of completion has been withdrawn:"
                + "\n"
                + "  "
                + task
        );
    }
}
