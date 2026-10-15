package alice.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task occurring between a start and end time.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy h:mm");
    private final LocalDateTime fromDateTime;
    private final LocalDateTime toDateTime;

    /**
     * Creates an event task.
     *
     * @param description The task description.
     * @param from The event start date and time.
     * @param to The event end date and time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        assert from != null && to != null : "Events must have both a start and an end time.";
        assert !to.isBefore(from) : "An event must not end before it starts.";
        this.fromDateTime = from;
        this.toDateTime = to;
    }

    /** Returns the event start date and time. */
    public LocalDateTime getFrom() {

        return fromDateTime;
    }

    /** Returns the event end date and time. */
    public LocalDateTime getTo() {

        return toDateTime;
    }

    /** Creates the first event occurrence scheduled after the supplied date and time. */
    @Override
    public Task createNextOccurrence(LocalDateTime currentDateTime) {
        LocalDateTime nextFrom = advanceDateTime(fromDateTime);
        LocalDateTime nextTo = advanceDateTime(toDateTime);
        while (!nextFrom.isAfter(currentDateTime)) {
            nextFrom = advanceDateTime(nextFrom);
            nextTo = advanceDateTime(nextTo);
        }
        Event nextEvent = new Event(getDescription(), nextFrom, nextTo);
        nextEvent.setRecurrence(getRecurrence());
        return nextEvent;
    }

    /** Advances the supplied date and time by this task's recurrence interval. */
    private LocalDateTime advanceDateTime(LocalDateTime dateTime) {
        return switch (getRecurrence()) {
            case DAILY -> dateTime.plusDays(1);
            case WEEKLY -> dateTime.plusWeeks(1);
            case MONTHLY -> dateTime.plusMonths(1);
            case YEARLY -> dateTime.plusYears(1);
        };
    }

    /**
     * Returns this event task in Alice's display format.
     *
     * @return The formatted event task.
     */
    @Override
    public String toString() {
        String recurrenceText = isRecurring() ? " (repeats: " + getRecurrence().getDisplayName() + ")" : "";
        return "[E]" + super.toString() + " (from: " + formatDateTime(fromDateTime)
                + " to: " + formatDateTime(toDateTime) + ")" + recurrenceText;
    }

    /** Formats a date and time with a lowercase meridiem indicator. */
    private String formatDateTime(LocalDateTime dateTime) {
        String meridiem = dateTime.getHour() < 12 ? "am" : "pm";
        return dateTime.format(DISPLAY_FORMAT) + " " + meridiem;
    }
}
