package squirtlebot.command;

import squirtlebot.exception.CommandException;
import squirtlebot.storage.Storage;
import squirtlebot.task.Task;
import squirtlebot.task.TaskList;
import squirtlebot.ui.Ui;

/**
 * Represents the unmark command within {@code SquirtleBot}.
 */
public class UnmarkCommand extends Command {
    private int index;

    /**
     * Constructs an UnmarkCommand object using user inputs.
     *
     * @param inputTokens array containing index value required to create an UnmarkCommand object.
     */
    public UnmarkCommand(String[] inputTokens) {
        setAttributes(inputTokens);
    }

    /**
     * Unmarks the task in the task list at the user-supplied index.
     * Displays a confirmation text to the user on command executed.
     *
     * @param tasks list containing tasks created previously by the user.
     * @param ui interface used to display output to the user.
     * @param storage storage handler used to persist changes made by the command.
     */
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        validateIndex(index, tasks);

        Task unmarkedTask = tasks.get(index);
        unmarkedTask.setDone(false);
        super.updateStorage(tasks, storage);

        assert !unmarkedTask.isDone();

        ui.setSavedMessage(String.format("\tThe following task was marked as not done:\n%s", unmarkedTask));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof UnmarkCommand otherUnmarkCommand) {
            return index == otherUnmarkCommand.index;
        } else {
            return false;
        }
    }

    /**
     * Extracts the index in the task list to unmark.
     *
     * @param inputTokens array containing user-supplied list index to unmark.
     * @throws CommandException if no index is provided, or provided index is not an integer.
     */
    private void setAttributes(String[] inputTokens) {
        try {
            index = Integer.parseInt(inputTokens[1]) - 1;
        } catch (NumberFormatException e) {
            throw new CommandException("Please enter a valid index :(");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new CommandException("Please enter an index to unmark :(", e);
        }
    }
}
