package miku.task;

import java.time.LocalDateTime;

import miku.parser.DateTimeParser;

/** A task that must be completed before a specified date or time. */
public class Deadline extends Task {
    private final LocalDateTime dueDateTime;
    private final boolean hasDueTime;

    /** Creates an undone deadline with its completion date or time. */
    public Deadline(String description, LocalDateTime dueDateTime, boolean hasDueTime) {
        super(TaskType.DEADLINE, description);
        assert dueDateTime != null : "Deadlines must have a date and time.";
        this.dueDateTime = dueDateTime;
        this.hasDueTime = hasDueTime;
    }

    /** Returns the deadline's typed date and time. */
    public LocalDateTime getDueDateTime() {
        return dueDateTime;
    }

    /** Returns whether the deadline was entered with an explicit time. */
    public boolean hasDueTime() {
        return hasDueTime;
    }

    /** Returns this deadline in Miku's task-list display format. */
    @Override
    public String toString() {
        return super.toString() + " (by: " + DateTimeParser.format(dueDateTime, hasDueTime) + ")";
    }
}
