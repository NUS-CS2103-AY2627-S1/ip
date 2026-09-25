package shan.command;

import shan.exception.DataFileException;
import shan.exception.InvalidArgumentException;
import shan.storage.Storage;
import shan.task.Task;
import shan.task.TaskList;

/**
 * Marks a task as done and persists the change.
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Constructs a command that marks the specified task as done.
     *
     * @param taskNumber One-based number of the task to mark.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the selected task as done, saves the change, and reports the result.
     *
     * @param tasks   Task list containing the task to mark.
     * @param storage Storage used to persist the updated task list.
     * @throws InvalidArgumentException If the task does not exist or is already completed.
     * @throws DataFileException        If the updated task list cannot be saved.
     */
    @Override
    public String execute(TaskList tasks, Storage storage)
            throws InvalidArgumentException, DataFileException {
        if (!tasks.containsTaskNumber(this.taskNumber)) {
            throw new InvalidArgumentException(
                    String.format("Task %d does not exist.", this.taskNumber));
        }

        Task task = tasks.get(this.taskNumber);
        if (task.isDone()) {
            throw new InvalidArgumentException(
                    String.format("Task %d is already marked as done.", this.taskNumber));
        }
        task.markDone();
        try {
            storage.save(tasks.snapshot());
        } catch (DataFileException exception) {
            task.unmarkDone();
            throw exception;
        }
        return String.format(
                "Well done! I have marked this task as done!\n  %s", task);
    }

    @Override
    public boolean isUndoable() {
        return true;
    }
}
