package zinc.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that must be completed by a specified date and time. */
public class Deadline extends Task {
    /** The format used when displaying deadline date-times to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH);
    /** The date and time by which this task must be completed. */
    private final LocalDateTime deadline;

    /**
     * Creates a deadline task.
     *
     * @param description The description of the task.
     * @param deadline The date and time by which the task must be completed.
     */
    public Deadline(String description, LocalDateTime deadline) {
        super(description);
        assert deadline != null : "Deadline date and time must not be null";
        this.deadline = deadline;
    }

    /**
     * Returns the category of this task.
     *
     * @return The deadline task type.
     */
    @Override
    public TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    /**
     * Returns a display-friendly representation including the deadline.
     *
     * @return The task name, completion status, and deadline.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: "
                + deadline.format(DISPLAY_DATE_TIME_FORMAT).toUpperCase(Locale.ENGLISH) + ")";
    }

    /** Returns the date and time by which this task must be completed. */
    public LocalDateTime getDeadline() {
        return deadline;
    }
}
