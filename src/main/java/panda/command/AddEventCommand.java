package panda.command;

import java.time.temporal.Temporal;

import panda.exception.ApplicationException;
import panda.task.Task;
import panda.task.TaskList;

/**
 * Adds an event task to Panda's task list.
 */
public class AddEventCommand extends AddTaskCommand {
    private final String description;
    private final Temporal startDateTime;
    private final Temporal endDateTime;

    /**
     * Creates a command for an event task.
     *
     * @param description Description of the event.
     * @param startDateTime Event start date and time.
     * @param endDateTime Event end date and time.
     */
    public AddEventCommand(String description, Temporal startDateTime, Temporal endDateTime) {
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * Adds the event task to the supplied task list.
     *
     * @param taskList Active task list.
     * @return The newly added {@link panda.task.Event} task.
     * @throws ApplicationException If the event range is invalid or clashes with another event.
     */
    @Override
    protected Task addTask(TaskList taskList) throws ApplicationException {
        return taskList.addEvent(description, startDateTime, endDateTime);
    }
}
