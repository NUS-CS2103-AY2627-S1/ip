package megatron.task;

import java.time.LocalDateTime;

/** Represents a task that must be completed by a specified time. */
public class Deadline extends Task {
    private LocalDateTime by;

    /**
     * Creates a deadline task.
     *
     * @param description the text describing the task
     * @param by the deadline text
     */
    public Deadline(String description, String by) {
        this(description, DateTimeParser.parse(by));
    }

    /** Creates a deadline task with a typed date and time. */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /** Changes this deadline to the supplied date and time. */
    public void reschedule(String newTime) {
        by = DateTimeParser.parse(newTime);
    }

    @Override
    public String toString() {
        return "[D][" + getStatusIcon() + "] " + description + " (by: " + DateTimeParser.format(by) + ")";
    }
}
