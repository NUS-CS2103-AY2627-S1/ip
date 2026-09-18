package computa.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import computa.util.DateTimeParser;

/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** Date or time by which the task should be completed. */
    protected LocalDateTime by;
    /** Original text is retained when the user enters a free-form date. */
    private final String byText;
    /** Whether the parsed value includes a time. */
    private final boolean hasTime;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description text describing the task.
     * @param by date or time by which the task should be completed.
     */
    public Deadline(String description, String by) {
        super(description);
        this.byText = by.trim();
        this.by = DateTimeParser.parse(this.byText);
        this.hasTime = DateTimeParser.hasTime(this.byText);
    }

    /**
     * Returns this deadline in the format used by the task data file.
     *
     * @return the deadline type, status, description, and due date/time.
     */
    @Override
    public String toFileFormat() {
        return "D" + super.toFileFormat() + " | " + getStorageBy();
    }

    /**
     * Formats this deadline for display in a task list.
     *
     * @return type icon, completion icon, description, and deadline.
     */
    @Override
    public String toString() {
        return "[D][" + getStatusIcon() + "] " + getDisplayDescription();
    }

    /** Returns the description together with the deadline. */
    @Override
    public String getDisplayDescription() {
        return description + " (by: " + getBy() + ")";
    }

    /** Returns whether another deadline has the same due date or time. */
    @Override
    protected boolean hasSameSchedule(Task other) {
        Deadline otherDeadline = (Deadline) other;
        return getBy().equals(otherDeadline.getBy());
    }

    /** Returns the deadline text. */
    public String getBy() {
        return by == null ? byText : DateTimeParser.formatForDisplay(by, hasTime);
    }

    /** Returns the normalized value used when writing this deadline to disk. */
    private String getStorageBy() {
        return by == null ? byText : DateTimeParser.formatForStorage(by, hasTime);
    }

    /** Returns whether this deadline falls on the supplied date. */
    @Override
    public boolean occursOn(LocalDate date) {
        return by != null && by.toLocalDate().equals(date);
    }

    /** Returns the parsed due date used when sorting tasks. */
    @Override
    public LocalDateTime getSortDate() {
        return by;
    }

    /** Returns the deadline type icon. */
    @Override
    public String getTypeIcon() {
        return "D";
    }
}
