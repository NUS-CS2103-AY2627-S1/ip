package yuno.command;

import yuno.exception.InvalidCommandFormatException;
import yuno.exception.YunoException;
import yuno.storage.Storage;
import yuno.task.Task;
import yuno.task.TaskList;
import yuno.ui.Ui;

/**
 * Represents a command that adds an event task.
 */
public class EventCommand extends Command {
    /**
     * Creates a command with the description, start time, and end time of an event.
     *
     * @param eventDetails Description and date-time text entered by the user.
     */
    public EventCommand(String eventDetails) {
        super(eventDetails);
    }

    /**
     * Adds an event task and displays its confirmation.
     *
     * @param taskList Task list to modify.
     * @param ui User interface used to display the confirmation.
     * @param storage Storage used to save the updated task list.
     * @return Always {@link CommandResult#CONTINUE}.
     * @throws YunoException If the task details are invalid or task data cannot be accessed.
     */
    @Override
    public CommandResult execute(TaskList taskList, Ui ui, Storage storage) throws YunoException {
        String eventDetails = getCommandArguments();
        String[] eventParts = eventDetails.split(" /from | /to ", 3);
        validateEventDetails(eventDetails, eventParts);
        assert eventParts.length == 3 : "Validated event must have three parts";
        TaskList updatedTaskList = taskList.copy();
        Task eventTask = updatedTaskList.addTask(
                eventParts[0],
                parseInputDateTime(eventParts[1]),
                parseInputDateTime(eventParts[2]));
        storage.save(updatedTaskList);
        taskList.replaceWith(updatedTaskList);
        ui.printAddTask(eventTask);
        return CommandResult.CONTINUE;
    }

    /**
     * Validates that the event fields contain a description and chronologically ordered dates.
     *
     * @param eventDetails Complete event details entered by the user.
     * @param eventParts Event fields to validate.
     * @throws InvalidCommandFormatException If any event details are missing, malformed, or out of order.
     */
    private void validateEventDetails(String eventDetails, String[] eventParts)
            throws InvalidCommandFormatException {
        if (eventParts[0].isBlank()) {
            throw new InvalidCommandFormatException("If you have no task, please don't bother me.");
        } else if (eventParts.length < 3 || eventParts[1].isBlank() || eventParts[2].isBlank()) {
            throw new InvalidCommandFormatException(
                    "If your task does not have a start and end time, "
                            + "save me some time and use another task type.");
        } else if (eventDetails.indexOf("/from") > eventDetails.indexOf("/to")) {
            throw new InvalidCommandFormatException(
                    "Any normal human would remember it as '/from' then '/to'. "
                            + "Check it before wasting my time.");
        } else if (!parseInputDateTime(eventParts[1]).isBefore(parseInputDateTime(eventParts[2]))) {
            throw new InvalidCommandFormatException(
                    "An event must end after it starts. "
                            + "Check the dates first before even submitting.");
        }
    }
}
