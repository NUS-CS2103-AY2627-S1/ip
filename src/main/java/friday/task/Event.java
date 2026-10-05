package friday.task;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import friday.FridayException;

/**
 * Represents a task that happens during a specific time period.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an event task with the given description and time period.
     *
     * @param description description of the event task
     * @param from start date and time
     * @param to end date and time
     * @throws FridayException if the start or end date and time cannot be parsed
     */
    public Event(String description, String from, String to) throws FridayException {
        super(description);
        this.from = TaskDateTime.parse(from);
        this.to = TaskDateTime.parse(to);

        if (!this.to.isAfter(this.from)) {
            throw new FridayException("Apologies, event end time must be after the start time sir");
        }

        assert this.from != null : "Event start time should have been parsed";
        assert this.to != null : "Event end time should have been parsed";
        assert this.to.isAfter(this.from) : "Event end time should be after start time";
    }

    /**
     * Returns the icon that shows this task is an event.
     *
     * @return event type icon
     */
    @Override
    public String getTaskTypeIcon() {
        return "E";
    }

    @Override
    protected Task createNextOccurrence(RepeatFrequency frequency) throws FridayException {
        LocalDateTime nextStart = frequency.advance(from);
        try {
            LocalDateTime nextEnd = nextStart.plus(Duration.between(from, to));
            return new Event(description, TaskDateTime.formatForFile(nextStart), TaskDateTime.formatForFile(nextEnd));
        } catch (DateTimeException e) {
            throw new FridayException("The next recurring event is outside the supported date range.");
        }
    }

    /**
     * Returns this event's start date and time.
     *
     * @return event start date and time
     */
    @Override
    public Optional<LocalDateTime> getReminderDateTime() {
        return Optional.of(from);
    }

    /**
     * Returns this event in the format used by the save file.
     *
     * @return save-file representation of the event
     */
    @Override
    public String toFileString() {
        return appendRepeatFrequency(getBaseFileString() + " | " + TaskDateTime.formatForFile(this.from)
                + " | " + TaskDateTime.formatForFile(this.to));
    }

    /**
     * Returns this event as text for display to the user.
     *
     * @return user-facing representation of the event
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + TaskDateTime.formatForDisplay(this.from)
                + " to: " + TaskDateTime.formatForDisplay(this.to) + ")";
    }
}
