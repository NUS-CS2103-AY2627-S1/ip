package niulai.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a task that starts and ends at specified dates or times.
 */
public class Event extends Task {
    /** The date or time when the event starts. */
    private final String from;

    /** The date or time when the event ends. */
    private final String to;

    /**
     * Creates a new incomplete event task.
     *
     * @param description the text describing the event
     * @param from the date or time when the event starts
     * @param to the date or time when the event ends
     */
    public Event(String description, String from, String to) {
        super(description);
        if (from == null || from.isBlank() || to == null || to.isBlank()) {
            throw new IllegalArgumentException("Event times cannot be blank.");
        }

        this.from = normalizeField(from);
        this.to = normalizeField(to);

        TemporalValue start = TemporalValue.parse(this.from);
        TemporalValue end = TemporalValue.parse(this.to);
        if (!TemporalValue.isEndStrictlyAfter(start, end)) {
            throw new IllegalArgumentException("An event must end after it starts.");
        }
    }

    /** @return the event task type */
    @Override
    public TaskType getType() {
        return TaskType.EVENT;
    }

    /**
     * Checks whether this event occurs on the specified date.
     *
     * <p>This works when both event endpoints contain supported date values. An event spanning
     * multiple dates matches every date from its start date through its end date.</p>
     *
     * @param date the date to check
     * @return whether this event occurs on the date
     */
    public boolean occursOn(LocalDate date) {
        if (date == null) {
            return false;
        }

        LocalDate startDate = getDate(from);
        LocalDate endDate = getDate(to);
        return startDate != null && endDate != null
                && !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /** Returns the description and endpoints used for duplicate detection. */
    @Override
    protected List<String> getIdentityFields() {
        return List.of(
                normalizeIdentityValue(getDescription()),
                normalizeIdentityValue(from),
                normalizeIdentityValue(to)
        );
    }

    /** Converts a supported date or date-time string into a date. */
    private static LocalDate getDate(String value) {
        LocalDateTime dateTime = Deadline.parseDateTimeValue(value);
        if (dateTime != null) {
            return dateTime.toLocalDate();
        }
        return Deadline.parseDateValue(value);
    }

    /**
     * Returns the escaped storage representation of this event.
     *
     * @return the base task fields followed by the event start and end values
     */
    @Override
    public String toStorageString() {
        return super.toStorageString() + " | " + escapeStorageField(from)
                + " | " + escapeStorageField(to);
    }

    /**
     * Returns the display representation of this event.
     *
     * @return the base task representation followed by the event interval
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
