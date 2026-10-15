package bobo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import bobo.util.DateUtil;

/**
 * Represents a task with a deadline date/time.
 */
public class Deadline extends Task {

    protected String byRaw;
    protected LocalDateTime byDateTime;
    protected LocalDate byDate;

    /**
     * Constructs a Deadline task with a description and a deadline string.
     * Attempts to parse the date/time into java.time objects.
     *
     * @param description Task description.
     * @param by          Deadline date/time string.
     */
    public Deadline(String description, String by) {
        super(description);
        this.byRaw = by;
        Object parsed = DateUtil.parseDateTimeOrDate(by);
        if (parsed instanceof LocalDateTime) {
            this.byDateTime = (LocalDateTime) parsed;
            this.byDate = this.byDateTime.toLocalDate();
        } else if (parsed instanceof LocalDate) {
            this.byDate = (LocalDate) parsed;
        }
    }

    /**
     * Returns formatted deadline string for display, or raw string if unparseable.
     *
     * @return Formatted deadline string.
     */
    public String getBy() {
        if (byDateTime != null) {
            return DateUtil.formatForDisplay(byDateTime);
        } else if (byDate != null) {
            return DateUtil.formatForDisplay(byDate);
        }
        return byRaw;
    }

    @Override
    public boolean isOnDate(LocalDate targetDate) {
        if (byDate != null && targetDate != null) {
            return byDate.equals(targetDate);
        }
        return false;
    }

    @Override
    public String toTextLine() {
        String byStorage = (byDateTime != null) ? DateUtil.formatForStorage(byDateTime)
                : (byDate != null) ? DateUtil.formatForStorage(byDate) : byRaw;
        return "D | " + super.toTextLine() + " | " + byStorage;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + getBy() + ")";
    }
}
