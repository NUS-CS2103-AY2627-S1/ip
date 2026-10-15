package command;

import exception.CbtException;
import task.Task;
import task.TaskList;

/** Marks a task as incomplete. */
public class UnmarkCommand implements Command {
    private final String taskNumber;

    /**
     * Creates a command that unmarks the specified one-based task number.
     *
     * @param taskNumber user-entered task number.
     */
    public UnmarkCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the selected task as incomplete.
     *
     * @param taskList task list to update.
     * @return result describing the unmarked task.
     * @throws CbtException if the task number is invalid or outside the list.
     */
    @Override
    public CommandResult execute(TaskList taskList) throws CbtException {
        Task task = taskList.unmarkTask(MarkCommand.toIndex(taskNumber));
        String message = "Back on the flight plan:\n" + "  " + task;
        return new CommandResult(message, true, false);
    }
}
