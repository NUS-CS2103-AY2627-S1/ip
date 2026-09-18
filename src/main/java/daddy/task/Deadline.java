package daddy.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Represents a task that must be completed by a specified date or time. */
public class Deadline extends Task {
    /** Formats deadline values for display in the task list. */
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy HH:mm");
    /** Stores the date and time by which this task must be completed. */
    private final LocalDateTime deadline;

    /**
     * Creates a deadline task.
     *
     * @param description the task description
     * @param deadline the date and time by which the task is due
     */
    public Deadline(String description, LocalDateTime deadline) {
        super(description, TaskType.DEADLINE);
        if (deadline == null) {
            throw new IllegalArgumentException("deadline must not be null");
        }
        this.deadline = deadline;
    }

    /**
     * Returns the description together with the formatted deadline.
     *
     * @return the description shown in Daddy's task list
     */
    @Override
    public String getDisplayDescription() {
        return getDescription() + " (by: " + deadline.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns the deadline value for persistence.
     *
     * @return the task's deadline date and time
     */
    public LocalDateTime getDeadline() {
        return deadline;
    }

    /**
     * Returns whether another task is a deadline with the same description and due date.
     *
     * @param other the task to compare
     * @return whether both deadlines represent the same work
     */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        return super.hasSameDetailsAs(other)
                && other instanceof Deadline otherDeadline
                && deadline.equals(otherDeadline.deadline);
    }

    /**
     * Returns whether this deadline falls on the supplied date.
     *
     * @param date the date to compare with the deadline
     * @return whether the deadline occurs on the supplied date
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return deadline.toLocalDate().equals(date);
    }
}
