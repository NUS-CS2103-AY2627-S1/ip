package laby.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import laby.Parser;

/** Represents a task that must be completed by a specific time. */
public class Deadline extends Task {
    private final LocalDateTime deadline;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description Description of the task.
     * @param deadline Time by which the task should be completed.
     */
    public Deadline(String description, LocalDateTime deadline) {
        this(description, deadline, false);
    }

    /**
     * Creates a deadline task with the supplied completion state.
     *
     * @param description Description of the task.
     * @param deadline Time by which the task should be completed.
     * @param isDone Whether the task is complete.
     */
    public Deadline(String description, LocalDateTime deadline, boolean isDone) {
        super(description, isDone);

        assert deadline != null : "deadline cannot be null";

        this.deadline = deadline;
    }

    /**
     * Returns the deadline in the format shown in the console.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy HH:mm", Locale.ENGLISH);
        return "[D]" + super.toString() + " (by: " + this.deadline.format(formatter) + ")";
    }

    /**
     * Returns the deadline in the format used for persistence.
     */
    @Override
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "D" + Parser.FIELD_SEPARATOR + super.toFileString() + Parser.FIELD_SEPARATOR
                + this.deadline.format(formatter) + "\n";
    }
}
