package shan.command;

import shan.exception.DataFileException;
import shan.exception.InvalidArgumentException;
import shan.storage.Storage;
import shan.task.Task;
import shan.task.TaskList;

/**
 * Marks a task as not done and persists the change.
 */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Constructs a command that marks the specified task as not done.
     *
     * @param taskNumber One-based number of the task to unmark.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the selected task as incomplete, saves the change, and reports the result.
     *
     * @param tasks   Task list containing the task to unmark.
     * @param storage Storage used to persist the updated task list.
     * @throws InvalidArgumentException If the task does not exist or is already incomplete.
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
        if (!task.isDone()) {
            throw new InvalidArgumentException(
                    String.format("Task %d is already marked as not done.", this.taskNumber));
        }
        task.unmarkDone();
        try {
            storage.save(tasks.snapshot());
        } catch (DataFileException exception) {
            task.markDone();
            throw exception;
        }
        return String.format(
                "What happened? I have unmarked this task as completed...\n  %s",
                task);
    }

    @Override
    public boolean isUndoable() {
        return true;
    }
}
