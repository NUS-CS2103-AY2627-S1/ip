package wobble.tasks;

import java.time.LocalDateTime;

import wobble.parser.DateTimeParser;

/** A task that must be completed by a stated date or time. */
public class Deadline extends Task {
    private final LocalDateTime by;

    /** Creates an unfinished deadline with its due date and time. */
    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        if (by == null) {
            throw new IllegalArgumentException("A deadline must have a due date");
        }
        assert by != null : "A deadline must have a due date";
        this.by = by;
    }

    /** Returns the deadline text. */
    public LocalDateTime getBy() {
        return by;
    }

    /** Returns whether this unfinished deadline has already passed. */
    public boolean isOverdue() {
        return !isDone() && by.isBefore(LocalDateTime.now());
    }

    /** Returns whether another deadline has the same description and due date. */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        return other instanceof Deadline && super.hasSameDetailsAs(other)
                && by.equals(((Deadline) other).by);
    }

    /** Returns this deadline's display representation. */
    @Override
    public String toString() {
        String overdueTag = isOverdue() ? "[OVERDUE] " : "";
        return getDisplayPrefix() + overdueTag + getDescription()
                + " (by: " + DateTimeParser.format(by) + ")";
    }
}
