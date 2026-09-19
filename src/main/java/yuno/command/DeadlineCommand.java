package yuno.command;

import yuno.exception.InvalidCommandFormatException;
import yuno.exception.YunoException;
import yuno.storage.Storage;
import yuno.task.Task;
import yuno.task.TaskList;
import yuno.ui.Ui;

/**
 * Represents a command that adds a deadline task.
 */
public class DeadlineCommand extends Command {
    /**
     * Creates a command with the description and deadline of a task to add.
     *
     * @param deadlineDetails Description and deadline text entered by the user.
     */
    public DeadlineCommand(String deadlineDetails) {
        super(deadlineDetails);
    }

    /**
     * Adds a deadline task and displays its confirmation.
     *
     * @param taskList Task list to modify.
     * @param ui User interface used to display the confirmation.
     * @param storage Storage used to save the updated task list.
     * @return Always {@link CommandResult#CONTINUE}.
     * @throws YunoException If the task description or deadline is missing, or task data cannot be accessed.
     */
    @Override
    public CommandResult execute(TaskList taskList, Ui ui, Storage storage) throws YunoException {
        String[] deadlineParts = getCommandArguments().split(" /by ", 2);
        validateDeadlineDetails(deadlineParts);
        assert deadlineParts.length == 2 : "Validated deadline must have two parts";
        TaskList updatedTaskList = taskList.copy();
        Task deadlineTask = updatedTaskList.addTask(
                deadlineParts[0], parseInputDateTime(deadlineParts[1]));
        storage.save(updatedTaskList);
        taskList.replaceWith(updatedTaskList);
        ui.printAddTask(deadlineTask);
        return CommandResult.CONTINUE;
    }

    /**
     * Validates that the deadline fields contain a task description and deadline.
     *
     * @param deadlineParts Deadline fields to validate.
     * @throws InvalidCommandFormatException If the task description or deadline is missing.
     */
    private void validateDeadlineDetails(String[] deadlineParts) throws InvalidCommandFormatException {
        if (deadlineParts[0].isBlank()) {
            throw new InvalidCommandFormatException("If you have no task, please don't bother me.");
        } else if (deadlineParts.length < 2 || deadlineParts[1].isBlank()) {
            throw new InvalidCommandFormatException(
                    "If you are not constrained by a date, use another task type.");
        }
    }
}
