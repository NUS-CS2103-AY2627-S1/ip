package unicorn.task;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Represents a task scheduled from one time detail to another.
 */
public class EventTask extends Task {
    private static final Pattern NUMERIC_DATE_TIME = Pattern.compile(
            "(?:\\d{4}-\\d{1,2}-\\d{1,2}|\\d{1,2}/\\d{1,2}/\\d{4})(?:\\s+\\d{4})?"
    );

    /** Detail describing when the event starts. */
    protected String from;
    /** Detail describing when the event ends. */
    protected String to;

    /**
     * Creates an event task.
     *
     * @param description task description
     * @param from event start detail
     * @param to event end detail
     * @throws IllegalArgumentException if numeric dates are invalid or the end is not after the start
     */
    public EventTask(String description, String from, String to) {
        super(description);
        assert from != null && !from.isBlank() : "Event start must not be blank";
        assert to != null && !to.isBlank() : "Event end must not be blank";

        validatePeriod(from, to);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start detail.
     *
     * @return event start detail
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event end detail.
     *
     * @return event end detail
     */
    public String getTo() {
        return to;
    }

    /**
     * Reports whether another event has the same description and period.
     *
     * @param other task to compare
     * @return {@code true} when both tasks represent the same event
     */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        return super.hasSameDetailsAs(other)
                && normalizeDetail(from).equals(normalizeDetail(((EventTask) other).from))
                && normalizeDetail(to).equals(normalizeDetail(((EventTask) other).to));
    }

    /**
     * Returns a display representation of this event task.
     *
     * @return task status, description, and event period
     */
    @Override
    public String toString() {
        return "[E] [" + getStatusIcon() + "] "
                + description
                + " (from: " + from + " to: " + to + ")";
    }

    private static void validatePeriod(String from, String to) {
        LocalDateTime start = parseNumericDate(from);
        LocalDateTime end = parseNumericDate(to);
        if (start != null && end != null && !start.isBefore(end)) {
            throw new IllegalArgumentException("Event end must be after its start");
        }
    }

    private static LocalDateTime parseNumericDate(String dateTimeText) {
        if (!NUMERIC_DATE_TIME.matcher(dateTimeText).matches()) {
            return null;
        }
        return DeadlineTask.parseBy(dateTimeText);
    }
}
