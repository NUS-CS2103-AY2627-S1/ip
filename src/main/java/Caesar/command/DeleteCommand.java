package caesar.command;

import caesar.exception.CaesarException;
import caesar.storage.Storage;
import caesar.task.Task;
import caesar.task.TaskList;
import caesar.ui.Ui;

/** Deletes one task and persists the updated task list. */
public class DeleteCommand extends Command {
    /** One-based task number selected by the user. */
    private final int taskNumber;

    /** Creates a delete command for the supplied one-based task number. */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Deletes the task, restoring it if saving fails, and reports success. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws CaesarException {
        int originalSize = tasks.size();
        Task removedTask = tasks.delete(taskNumber);
        // Deleting a task must remove exactly one item from the list.
        assert tasks.size() == originalSize - 1 : "Deleting a task must decrease the list size by one";
        try {
            storage.save(tasks);
        } catch (CaesarException exception) {
            tasks.insert(taskNumber, removedTask);
            // A failed save must restore both the size and the deleted position.
            assert tasks.size() == originalSize : "A failed delete must restore the original list size";
            assert tasks.get(taskNumber) == removedTask : "A failed delete must restore the deleted task";
            throw exception;
        }
        ui.showTaskDeleted(removedTask, tasks);
    }
}
