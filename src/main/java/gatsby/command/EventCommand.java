package gatsby.command;

import java.time.LocalDateTime;

import gatsby.exception.EmptyPayloadException;
import gatsby.exception.GatsbyException;
import gatsby.model.Event;
import gatsby.model.TaskList;
import gatsby.ui.Ui;

/**
 * Creates and saves an event task.
 */
public class EventCommand extends Command {
    /** The description and time range entered after the {@code event} command. */
    private final String payload;

    /**
     * Creates an event command for the supplied payload.
     *
     * @param payload the text entered after {@code event}
     */
    public EventCommand(String payload) {
        this.payload = payload == null ? "" : payload.strip();
    }

    /**
     * Validates, parses, creates, saves, and reports the new event.
     *
     * @param tasks the current task list
     * @param ui the console interaction handler
     * @throws GatsbyException when the description or event times are missing or invalid
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws GatsbyException {
        String[] eventParts = splitOnKeyword(payload, "/from",
                " son there's no event name/timing for this event -_-!");
        String[] timeParts = splitOnKeyword(eventParts[1], "/to",
                " son this event has no end time, it's infinite! -_-!");
        String description = requireText(eventParts[0],
                " son this event has no description -_-!");
        String start = requireText(timeParts[0],
                " son this event has no start time after /from -_-!");
        String end = requireText(timeParts[1],
                " son this event has no end time after /to -_-!");
        LocalDateTime startDate = parseDateTime(start, "event start date and time");
        LocalDateTime endDate = parseDateTime(end, "event end date and time");
        if (!startDate.isBefore(endDate)) {
            throw new EmptyPayloadException(" OOPS! An event's end time must be later than its start time.");
        }
        addTask(tasks, ui, new Event(description, startDate, endDate));
    }
}
