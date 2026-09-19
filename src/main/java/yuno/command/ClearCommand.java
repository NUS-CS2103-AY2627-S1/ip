package yuno.command;

import yuno.exception.InvalidCommandFormatException;
import yuno.exception.YunoException;
import yuno.storage.Storage;
import yuno.task.TaskList;
import yuno.ui.Ui;

/**
 * Represents a command that removes every task from the task list.
 */
public class ClearCommand extends Command {
    /**
     * Creates a command that clears the task list.
     *
     * @param commandArguments Additional command arguments, which must be blank.
     */
    public ClearCommand(String commandArguments) {
        super(commandArguments);
    }

    /**
     * Clears the task list, saves the empty list, and displays a confirmation.
     *
     * @param taskList Task list to clear.
     * @param ui User interface used to display the confirmation.
     * @param storage Storage used to save the empty task list.
     * @return Always {@link CommandResult#CONTINUE}.
     * @throws YunoException If additional command data is supplied or the empty task list cannot be saved.
     */
    @Override
    public CommandResult execute(TaskList taskList, Ui ui, Storage storage) throws YunoException {
        if (!getCommandArguments().isBlank()) {
            throw new InvalidCommandFormatException(
                    "If you want everything gone, type only 'clear'. "
                            + "Don't make me guess what those extra words mean.");
        }
        TaskList updatedTaskList = taskList.copy();
        updatedTaskList.clearTasks();
        storage.save(updatedTaskList);
        taskList.replaceWith(updatedTaskList);
        ui.printTasksCleared();
        return CommandResult.CONTINUE;
    }
}
