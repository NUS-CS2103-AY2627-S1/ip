package caesar.command;

import caesar.exception.CaesarException;
import caesar.storage.Storage;
import caesar.task.Task;
import caesar.task.TaskList;
import caesar.ui.Ui;

/** Marks one task as complete and persists the updated task list. */
public class MarkCommand extends Command {
    /** One-based task number selected by the user. */
    private final int taskNumber;

    /** Creates a mark command for the supplied one-based task number. */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Marks the task, rolls back on a save failure, and reports success. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws CaesarException {
        Task task = tasks.get(taskNumber);
        tasks.mark(taskNumber);
        // A successful mark operation must leave the selected task complete.
        assert task.isDone() : "A successfully marked task must be complete";
        try {
            storage.save(tasks);
        } catch (CaesarException exception) {
            task.markAsNotDone();
            // A failed save must restore the task's status before the command.
            assert !task.isDone() : "A failed mark must restore the pending status";
            throw exception;
        }
        ui.showTaskMarked(task);
    }
}
