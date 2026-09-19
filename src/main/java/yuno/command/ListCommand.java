package yuno.command;

import yuno.exception.InvalidCommandFormatException;
import yuno.exception.YunoException;
import yuno.storage.Storage;
import yuno.task.TaskList;
import yuno.ui.Ui;

/**
 * Represents a command that displays all stored tasks.
 */
public class ListCommand extends Command {
    /**
     * Creates a command that displays the task list.
     *
     * @param commandArguments Additional command arguments, which must be blank.
     */
    public ListCommand(String commandArguments) {
        super(commandArguments);
    }

    /**
     * Displays all tasks in the task list.
     *
     * @param taskList Task list to display.
     * @param ui User interface used to display the tasks.
     * @param storage Storage that is not modified.
     * @return Always {@link CommandResult#CONTINUE}.
     * @throws YunoException If additional command data is supplied or a task cannot be retrieved.
     */
    @Override
    public CommandResult execute(TaskList taskList, Ui ui, Storage storage) throws YunoException {
        if (!getCommandArguments().isBlank()) {
            throw new InvalidCommandFormatException(
                    "I only need 'list' to show your tasks. What am I supposed to do with the rest?");
        }
        ui.printList(taskList);
        return CommandResult.CONTINUE;
    }
}
