package command;

import exception.CbtException;
import task.Task;
import task.TaskList;

/** Marks a task as complete. */
public class MarkCommand implements Command {
    private final String taskNumber;

    /**
     * Creates a command that marks the specified one-based task number.
     *
     * @param taskNumber user-entered task number.
     */
    public MarkCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the selected task as completed.
     *
     * @param taskList task list to update.
     * @return result describing the marked task.
     * @throws CbtException if the task number is invalid or outside the list.
     */
    @Override
    public CommandResult execute(TaskList taskList) throws CbtException {
        Task task = taskList.markTask(toIndex(taskNumber));
        String message = "Mission accomplished. Task complete:\n"
                + "  " + task;
        return new CommandResult(message, true, false);
    }

    /**
     * Converts a positive, one-based task number to a zero-based index.
     *
     * @param value user-entered task number.
     * @return zero-based task index.
     * @throws CbtException if the value is not a positive integer.
     */
    static int toIndex(String value) throws CbtException {
        String normalizedValue = value.trim();
        if (!normalizedValue.matches("[1-9]\\d*")) {
            throw new CbtException("Please enter a task number from the list, e.g. mark 1.");
        }
        try {
            return Integer.parseInt(normalizedValue) - 1;
        } catch (NumberFormatException exception) {
            // Fall through to the common validation error.
        }
        throw new CbtException("Please enter a task number from the list, e.g. mark 1.");
    }
}
