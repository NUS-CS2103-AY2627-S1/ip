package squirtlebot.command;

import squirtlebot.exception.CommandException;
import squirtlebot.storage.Storage;
import squirtlebot.task.Event;
import squirtlebot.task.Task;
import squirtlebot.task.TaskList;
import squirtlebot.ui.Ui;


/**
 * Represents the confirm command in {@code SquirtleBot}, allowing users to
 * confirm a single start/end date for an event.
 */
public class ConfirmEventDateCommand extends Command {
    private int taskIndex;
    private int confirmedDateIndex;


    /**
     * Constructs a new ConfirmEventDateCommand using inputs provided by a user.
     *
     * @param inputTokens array containing user inputs required to confirm the date of an event.
     */
    public ConfirmEventDateCommand(String[] inputTokens) {
        setAttributes(inputTokens);
    }

    /**
     * Retrieves the event to confirm from the task list.
     * Verifies if the selected task is an Event.
     * Sets the date of an unconfirmed event according to user's input.
     * If event's date is already confirmed, no changes are made.
     *
     * @param tasks list containing tasks created previously by the user.
     * @param ui interface used to display output to the user.
     * @param storage storage handler used to persist changes made by the command.
     * @throws CommandException if selected task to confirm is not an {@link Event}.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {

        validateIndex(taskIndex, tasks);

        Task task = tasks.get(taskIndex);
        if (!(task instanceof Event eventToConfirm)) {
            throw new CommandException("Selected event was not an Event!");
        }

        eventToConfirm.confirmEventDate(confirmedDateIndex);

        ui.setSavedMessage("Event updated:\n" + eventToConfirm);

        updateStorage(tasks, storage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof ConfirmEventDateCommand otherConfirmEventDateCommand) {
            boolean areTaskIndicesEqual = taskIndex == otherConfirmEventDateCommand.taskIndex;
            boolean areConfirmedDateIndicesEqual = confirmedDateIndex
                    == otherConfirmEventDateCommand.confirmedDateIndex;

            return areTaskIndicesEqual && areConfirmedDateIndicesEqual;
        } else {
            return false;
        }
    }

    /**
     * Extracts two indices required to confirm the date for an event.
     *
     * @param inputTokens array containing two indices required to confirm an event's date.
     * @throws CommandException if no indices are provided, or provided indices are not integers.
     */
    private void setAttributes(String[] inputTokens) {
        try {
            taskIndex = Integer.parseInt(inputTokens[1]) - 1;
            confirmedDateIndex = Integer.parseInt(inputTokens[2]) - 1;
        } catch (NumberFormatException e) {
            throw new CommandException("Please enter a valid index :(", e);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new CommandException("Please enter an index to confirm dates for :(", e);
        }
    }
}
