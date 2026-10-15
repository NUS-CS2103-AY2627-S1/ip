package squirtlebot.command;

import squirtlebot.exception.CommandException;
import squirtlebot.storage.Storage;
import squirtlebot.task.Task;
import squirtlebot.task.TaskList;
import squirtlebot.ui.Ui;

/**
 * Represents the delete command within {@code SquirtleBot}.
 */
public class DeleteCommand extends Command {
    private int index;

    /**
     * Constructs a DeleteCommand object using user inputs.
     *
     * @param inputTokens array containing index value required for creating a DeleteCommand object.
     */
    public DeleteCommand(String[] inputTokens) {
        setAttributes(inputTokens);
    }

    /**
     * Deletes a user-specified task from task list.
     *
     * @param tasks list containing tasks created previously by the user.
     * @param ui interface used to display output to the user.
     * @param storage storage handler used to persist changes made by the command.
     */
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        int sizeBeforeRemoval = tasks.size();

        validateIndex(index, tasks);

        Task removedTask = tasks.remove(index);
        ui.setSavedMessage(String.format("\tThe following task was removed:\n%s", removedTask));

        assert sizeBeforeRemoval == tasks.size() + 1;

        super.updateStorage(tasks, storage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof DeleteCommand otherDeleteCommand) {
            return index == otherDeleteCommand.index;
        } else {
            return false;
        }
    }

    /**
     * Extracts the index to delete from an array of user inputs.
     *
     * @param inputTokens array containing the task list index to delete from.
     * @throws CommandException if no index is provided, or provided index is not an integer.
     */
    private void setAttributes(String[] inputTokens) {
        try {
            index = Integer.parseInt(inputTokens[1]) - 1;
        } catch (NumberFormatException e) {
            throw new CommandException("Please enter a valid index :(", e);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new CommandException("Please enter an index to delete :(", e);
        }
    }
}
