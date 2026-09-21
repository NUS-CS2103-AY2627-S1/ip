package topaz.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu HHmm", Locale.ENGLISH);
    private final LocalDateTime by;
    private final boolean hasTime;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description the task description
     * @param by the deadline date and time
     * @param hasTime whether the input included a time component
     */
    public Deadline(String description, LocalDateTime by, boolean hasTime) {
        super(description);
        // Parsed deadlines always have a time value; formatting and persistence rely on it.
        assert by != null : "A deadline must have a date and time.";
        this.by = by;
        this.hasTime = hasTime;
    }

    /**
     * Returns the deadline task type.
     *
     * @return the deadline task type
     */
    @Override
    public TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    /**
     * Returns the description together with the formatted deadline.
     *
     * @return the formatted deadline description
     */
    @Override
    public String getDescription() {
        String formattedBy = hasTime ? by.format(DISPLAY_FORMAT) : by.toLocalDate().format(
                DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH));
        return super.getDescription() + " (by: " + formattedBy + ")";
    }

    @Override
    protected boolean hasSameSchedule(Task other) {
        return by.equals(((Deadline) other).by);
    }

    /**
     * Serializes this deadline for storage.
     *
     * @return the deadline in save-file format
     */
    @Override
    public String toFileString() {
        String savedBy = hasTime ? by.toString() : by.toLocalDate().toString();
        return "D | " + (isDone() ? "1" : "0") + " | " + super.getDescription() + " | " + savedBy;
    }
}
