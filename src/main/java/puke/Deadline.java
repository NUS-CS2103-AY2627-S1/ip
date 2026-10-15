package puke;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Represents a task that must be completed by a specific date. */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");
    protected LocalDate deadline;

    /**
     * Creates a deadline from an ISO-8601 date.
     *
     * @param desc Deadline description.
     * @param deadline Deadline date.
     */
    public Deadline(String desc, String deadline) {
        super(desc);
        this.deadline = LocalDate.parse(deadline);
        this.type = TaskType.DEADLINE;
    }

    /**
     * Creates a deadline with the given completion state.
     *
     * @param desc Deadline description.
     * @param deadline Deadline date.
     * @param done Whether the deadline is complete.
     */
    public Deadline(String desc, String deadline, boolean done) {
        this(desc, deadline);
        this.isDone = done;
    }

    @Override
    public String toString() {
        return String.format("[D][%s] %s (by: %s)", getStatusIcon(), desc, deadline.format(DISPLAY_FORMAT));
    }

    public String getDeadline() {
        return deadline.toString();
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && other instanceof Deadline otherDeadline
                && deadline.equals(otherDeadline.deadline);
    }
}
