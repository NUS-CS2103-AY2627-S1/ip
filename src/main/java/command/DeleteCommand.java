package command;

import exception.CbtException;
import task.Task;
import task.TaskList;

/** Removes one task from the list. */
public class DeleteCommand implements Command {
    private final String taskNumber;

    /**
     * Creates a command that deletes the specified one-based task number.
     *
     * @param taskNumber user-entered task number.
     */
    public DeleteCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes the selected task from the task list.
     *
     * @param tasks task list to update.
     * @return result describing the deleted task.
     * @throws CbtException if the task number is invalid or outside the list.
     */
    @Override
    public CommandResult execute(TaskList tasks) throws CbtException {
        Task removedTask = tasks.deleteTask(MarkCommand.toIndex(taskNumber));
        String message = "Cleared from the flight plan:\n"
                + "  " + removedTask + "\n" + tasks.getSize() + " task"
                + (tasks.getSize() == 1 ? " remains" : "s remain") + " in orbit.";
        return new CommandResult(message, true, false);
    }
}
