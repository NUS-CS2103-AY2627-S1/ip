package bobo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import bobo.util.DateUtil;

/**
 * Represents an event task with start and end dates/times.
 */
public class Event extends Task {

    protected String fromRaw;
    protected String toRaw;
    protected LocalDateTime fromDateTime;
    protected LocalDate fromDate;
    protected LocalDateTime toDateTime;
    protected LocalDate toDate;

    /**
     * Constructs an Event task with description, start date/time, and end
     * date/time.
     * Attempts to parse start and end strings into java.time objects.
     *
     * @param description Task description.
     * @param from        Start date/time string.
     * @param to          End date/time string.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.fromRaw = from;
        this.toRaw = to;

        Object parsedFrom = DateUtil.parseDateTimeOrDate(from);
        if (parsedFrom instanceof LocalDateTime) {
            this.fromDateTime = (LocalDateTime) parsedFrom;
            this.fromDate = this.fromDateTime.toLocalDate();
        } else if (parsedFrom instanceof LocalDate) {
            this.fromDate = (LocalDate) parsedFrom;
        }

        Object parsedTo = DateUtil.parseDateTimeOrDate(to);
        if (parsedTo instanceof LocalDateTime) {
            this.toDateTime = (LocalDateTime) parsedTo;
            this.toDate = this.toDateTime.toLocalDate();
        } else if (parsedTo instanceof LocalDate) {
            this.toDate = (LocalDate) parsedTo;
        }
    }

    /**
     * Returns formatted start string for display, or raw string if unparseable.
     *
     * @return Formatted start date/time string.
     */
    public String getFrom() {
        if (fromDateTime != null) {
            return DateUtil.formatForDisplay(fromDateTime);
        } else if (fromDate != null) {
            return DateUtil.formatForDisplay(fromDate);
        }
        return fromRaw;
    }

    /**
     * Returns formatted end string for display, or raw string if unparseable.
     *
     * @return Formatted end date/time string.
     */
    public String getTo() {
        if (toDateTime != null) {
            return DateUtil.formatForDisplay(toDateTime);
        } else if (toDate != null) {
            return DateUtil.formatForDisplay(toDate);
        }
        return toRaw;
    }

    @Override
    public boolean isOnDate(LocalDate targetDate) {
        if (targetDate == null) {
            return false;
        }
        if (fromDate != null && toDate != null) {
            return (!fromDate.isAfter(targetDate)) && (!toDate.isBefore(targetDate));
        } else if (fromDate != null) {
            return fromDate.equals(targetDate);
        } else if (toDate != null) {
            return toDate.equals(targetDate);
        }
        return false;
    }

    @Override
    public String toTextLine() {
        String fromStorage = (fromDateTime != null) ? DateUtil.formatForStorage(fromDateTime)
                : (fromDate != null) ? DateUtil.formatForStorage(fromDate) : fromRaw;
        String toStorage = (toDateTime != null) ? DateUtil.formatForStorage(toDateTime)
                : (toDate != null) ? DateUtil.formatForStorage(toDate) : toRaw;
        return "E | " + super.toTextLine() + " | " + fromStorage + " | " + toStorage;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + getFrom() + " to: " + getTo() + ")";
    }
}
