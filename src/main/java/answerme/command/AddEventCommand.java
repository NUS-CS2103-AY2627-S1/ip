package answerme.command;

import java.time.LocalDateTime;

import answerme.task.Event;

/**
 * Represents a command that adds an event task to the task list.
 */
public class AddEventCommand extends AddCommand {
    /**
     * Creates a command that adds an event task with the specified details.
     *
     * @param description The description of the event task.
     * @param eventStart The event start date and time.
     * @param eventEnd The event end date and time.
     */
    public AddEventCommand(String description, LocalDateTime eventStart, LocalDateTime eventEnd) {
        super(new Event(description, eventStart, eventEnd));
    }
}
