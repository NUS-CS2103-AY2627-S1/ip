package shan.command;

import shan.exception.DataFileException;
import shan.exception.InvalidArgumentException;
import shan.storage.Storage;
import shan.task.Task;
import shan.task.TaskList;

/**
 * Deletes a task and persists the updated task list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Constructs a command that deletes the specified task.
     *
     * @param taskNumber One-based number of the task to delete.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes the selected task, saves the updated list, and reports the result.
     *
     * @param tasks   Task list to update.
     * @param storage Storage used to persist the updated task list.
     * @throws InvalidArgumentException If the task number does not exist.
     * @throws DataFileException        If the updated task list cannot be saved.
     */
    @Override
    public String execute(TaskList tasks, Storage storage)
            throws InvalidArgumentException, DataFileException {
        if (!tasks.containsTaskNumber(this.taskNumber)) {
            throw new InvalidArgumentException(
                    String.format("Task %d does not exist.", this.taskNumber));
        }

        Task removedTask = tasks.delete(this.taskNumber);
        try {
            storage.save(tasks.snapshot());
        } catch (DataFileException exception) {
            tasks.restore(this.taskNumber, removedTask);
            throw exception;
        }
        return String.format(
                "Noted. I've removed this task:\n  %s\nNow you have %d tasks.",
                removedTask, tasks.size());
    }

    @Override
    public boolean isUndoable() {
        return true;
    }
}
