package jarvis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Represents a task that must be completed by a stated date or time. */
public class Deadline extends Task {
    /** Format used for presenting deadline dates in the user interface. */
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    /** Date by which this task should be completed. */
    private LocalDate by;

    /** Creates an incomplete deadline task. */
    public Deadline(String description, LocalDate by) {
        super(description);
        assert by != null : "Deadline date must not be null";
        this.by = by;
    }

    /** Returns the deadline date. */
    public LocalDate getBy() {
        return by;
    }

    /** Changes this deadline's due date, allowing the task to be snoozed. */
    public void snoozeUntil(LocalDate newDate) {
        assert newDate != null : "Snooze date must not be null";
        by = newDate;
    }

    /** Returns the deadline date in the format shown to the user. */
    public String getFormattedBy() {
        return by.format(DISPLAY_FORMAT);
    }

    /** Returns the type marker used when displaying a deadline. */
    @Override
    protected String getTypeIcon() {
        return "D";
    }

    /** Returns the task followed by its formatted deadline date. */
    @Override
    public String toString() {
        return super.toString() + " (by: " + getFormattedBy() + ")";
    }
}
