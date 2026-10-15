package pulbot.task;

import java.time.LocalDateTime;

import pulbot.Pulbot;

/** Represents a task that must be completed by a specified date and time. */
public class Deadline extends Task {
    private final LocalDateTime by;

    public Deadline(String description, String by) {
        this(description, Pulbot.parseDateTime(by));
    }

    /** Creates a deadline with an already parsed date and time. */
    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        assert by != null : "A deadline must have a parsed date and time";
        this.by = by;
    }

    /** Returns the deadline date and time. */
    public LocalDateTime getBy() {
        return by;
    }

    /** Returns whether another deadline has the same due date and time. */
    @Override
    protected boolean hasSameSchedule(Task other) {
        return other instanceof Deadline deadline && by.equals(deadline.by);
    }

    /** Returns the user-facing representation including the deadline. */
    @Override
    public String toString() {
        return super.toString() + " (by: " + Pulbot.formatDateTime(by) + ")";
    }
}
