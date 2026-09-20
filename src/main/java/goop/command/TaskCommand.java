package goop.command;

import java.io.IOException;

import goop.exception.GoopException;
import goop.storage.Storage;
import goop.task.TaskList;

/**
 * Provides validation and completion updates for commands that target one task.
 */
public abstract class TaskCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command targeting a one-based task number entered by the user.
     *
     * @param taskNumber One-based task number.
     */
    protected TaskCommand(int taskNumber) {
        assert taskNumber > 0 : "The parser must supply a positive task number";
        this.taskNumber = taskNumber;
    }

    /**
     * Converts the stored task number into a valid zero-based list index.
     *
     * @param tasks Current task list.
     * @param commandWord Command name used in error guidance.
     * @return Validated zero-based task index.
     * @throws GoopException If the task list is empty or the number is too large.
     */
    protected int getTaskIndex(TaskList tasks, String commandWord) throws GoopException {
        if (tasks.size() == 0) {
            throw new GoopException("There are no tasks to " + commandWord
                    + ". Add a task first.");
        }
        if (taskNumber > tasks.size()) {
            throw new GoopException("Task " + taskNumber
                    + " is outside the list. Run list and choose a number from 1 to "
                    + tasks.size() + ".");
        }
        return taskNumber - 1;
    }

    /**
     * Persists a completion change and restores the previous state if saving fails.
     *
     * @param tasks Task list containing the selected task.
     * @param taskIndex Validated zero-based task index.
     * @param isDone Completion state to apply.
     * @param storage Storage used to save the updated list.
     * @throws IOException If the changed list cannot be saved.
     */
    protected void setDoneAndSave(TaskList tasks, int taskIndex, boolean isDone, Storage storage)
            throws IOException {
        boolean wasDone = tasks.get(taskIndex).isDone();
        tasks.setDone(taskIndex, isDone);
        try {
            storage.saveTasks(tasks);
        } catch (IOException error) {
            tasks.setDone(taskIndex, wasDone);
            throw error;
        }
    }
}
