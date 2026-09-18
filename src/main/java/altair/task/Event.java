package altair.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A task with a start date and an end date.
 */
public class Event extends Task {

    /** The date on which the event starts. */
    protected LocalDate from;

    /** The date on which the event ends. */
    protected LocalDate to;

    /** The format used when showing dates to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /**
     * Creates an unfinished event.
     *
     * @param description the event description.
     * @param from the date on which the event starts.
     * @param to the date on which the event ends.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        // Both dates are always produced by LocalDate.parse in the caller,
        // which returns a value or throws; a null here means a caller bug.
        assert from != null && to != null
                : "event start and end dates should be parsed before construction";
        this.from = from;
        this.to = to;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link TaskType#EVENT}
     */
    @Override
    protected TaskType getTaskType() {
        return TaskType.EVENT;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Two Events with the same description are duplicates only if both their
     * start and end dates match too; a different date range means they are
     * different commitments.</p>
     */
    @Override
    protected boolean hasSameSchedule(Task other) {
        Event otherEvent = (Event) other;
        return from.equals(otherEvent.from) && to.equals(otherEvent.to);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Appends the start and end dates as {@code from - to}, so the saved line is
     * {@code E | done | description | yyyy-MM-dd - yyyy-MM-dd}.</p>
     */
    @Override
    public String toFileString() {
        return super.toFileString() + " | " + from + " - " + to;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Adds the date range in {@code MMM dd yyyy} form, e.g.
     * {@code (from: Oct 15 2025 to: Oct 16 2025)}.</p>
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from.format(DISPLAY_DATE_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
