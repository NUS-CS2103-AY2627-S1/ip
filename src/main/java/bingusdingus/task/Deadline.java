package bingusdingus.task;

import java.time.LocalDateTime;
import java.util.Objects;

import bingusdingus.parser.DateTimeParser;

/** Represents a task that must be completed by a specified date or time. */
public class Deadline extends Task {
    private final LocalDateTime deadline;

    /** Creates a deadline task from a user-entered date or date/time. */
    public Deadline(String description, String deadline) {
        this(description, DateTimeParser.parse(deadline));
    }

    /** Creates a deadline task from an already parsed date/time. */
    public Deadline(String description, LocalDateTime deadline) {
        super(description);
        this.deadline = Objects.requireNonNull(deadline, "deadline");
    }

    /** Returns the deadline task's display text. */
    @Override
    public String toString() {
        return "[D][" + this.getStatusIcon() + "] " + this.getDescription()
                + " (by: " + DateTimeParser.format(this.deadline) + ")";
    }

    /** Returns whether this deadline has the same description and date as the supplied task. */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        return super.hasSameDetailsAs(other)
                && deadline.equals(((Deadline) other).deadline);
    }

    /** Returns the deadline task in the format used by the task storage file. */
    @Override
    public String toFileFormat() {
        return getBaseFileFormat("D") + " | " + DateTimeParser.formatForStorage(this.deadline);
    }
}
