package yuno.command;

import yuno.exception.InvalidCommandFormatException;
import yuno.exception.YunoException;
import yuno.storage.Storage;
import yuno.task.Task;
import yuno.task.TaskList;
import yuno.ui.Ui;

/**
 * Represents a command that adds a to-do task.
 */
public class TodoCommand extends Command {
    /**
     * Creates a command with the description of a to-do task to add.
     *
     * @param description Description of the task to add.
     */
    public TodoCommand(String description) {
        super(description);
    }

    /**
     * Adds a to-do task and displays its confirmation.
     *
     * @param taskList Task list to modify.
     * @param ui User interface used to display the confirmation.
     * @param storage Storage used to save the updated task list.
     * @return Always {@link CommandResult#CONTINUE}.
     * @throws YunoException If the task description is blank or task data cannot be accessed.
     */
    @Override
    public CommandResult execute(TaskList taskList, Ui ui, Storage storage) throws YunoException {
        if (getCommandArguments().isBlank()) {
            throw new InvalidCommandFormatException("If you have no task, please don't bother me.");
        }
        TaskList updatedTaskList = taskList.copy();
        Task todoTask = updatedTaskList.addTask(getCommandArguments());
        storage.save(updatedTaskList);
        taskList.replaceWith(updatedTaskList);
        ui.printAddTask(todoTask);
        return CommandResult.CONTINUE;
    }
}
